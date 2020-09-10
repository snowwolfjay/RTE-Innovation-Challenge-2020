import React, { Component } from 'react';
import PropTypes from 'prop-types';
import prettyBytes from 'pretty-bytes';
import { withRouter } from 'react-router-dom';
import Icon from './common/Icon';
import Button from './common/Button';
import Input from './common/Input';
import FileBox from './FileBox';
import Toast from './common/Toast';
import {
  prepareRecv,
  deleteRecvCode,
} from '../actions/file';
import Peer from '../Peer';
import {
  calcPercent,
} from '../common/util';

import styles from './RecvFilePanel.cm.styl';

class RecvFilePanel extends Component {
  constructor(props) {
    super(props);
    this.onChangeRecvCode = this.onChangeRecvCode.bind(this);
    this.onPrepareRecv = this.onPrepareRecv.bind(this);
    this.onStartRecv = this.onStartRecv.bind(this);
    this.onReset = this.onReset.bind(this); // 回到初始状态，比如点击取消或者接收完成继续接收等

    this.onRecvPeerData = this.onRecvPeerData.bind(this);
    this.handlePeerMsg = this.handlePeerMsg.bind(this);

    this.peer = new Peer();
    this.recvBuffer = [];
    this.recvSizes = {};
    this.bps = 0;

    this.timer = setInterval(() => {
      const files = this.props.files.map(f => {
        const recvSize = this.recvSizes[f.uid] || 0;
        const pct = calcPercent(recvSize, f.size);
        return {
          ...f,
          pct,
        };
      });
      this.props.setState({
        files,
      });
      this.bps = 0;
    }, 1000);
  }

  componentDidMount() {
    document.title = 'Receive';
    const recvCode = this.props.match.params.recvCode;
    if (recvCode) {
      prepareRecv(recvCode);
    }
  }

  componentWillUnMount() {
    clearInterval(this.timer);
  }

  onChangeRecvCode(value) {
    this.props.setState({ recvCode: value });
  }

  onPrepareRecv() {
    prepareRecv(this.props.recvCode);
  }

  onReset() {
    this.peer.destroy();
    this.props.setState({
      recvCode: '',
      peerState: '',
      started: false,
      files: [],
      targetId: '',
    });
  }

  onStartRecv() {
    this.props.setState({
      started: true,
    });

    const peer = this.peer;

    peer.on('connecting', () => {
      this.props.setState({
        peerState: 'connecting',
      });
    });

    peer.on('connected', () => {
      Toast.success('连接成功');
      this.props.setState({
        peerState: 'connected',
      });
    });

    peer.on('disconnected', () => {
      this.props.setState({
        peerState: 'disconnected',
      });
    });

    peer.on('connectFailed', () => {
      Toast.error('连接失败，请重试');
      this.props.setState({
        peerState: 'connectFailed',
      });
    });

    peer.on('channelOpen', () => {
      this.props.setState({
        peerState: 'transfer',
      });
      // 收件码只能使用一次，一旦开始接收就使其失效
      deleteRecvCode(this.props.recvCode || this.props.match.params.recvCode);
    });

    peer.on('data', this.onRecvPeerData);

    peer.connectPeer(this.props.targetId);
  }

  onRecvPeerData(data) {
    const {
      curFileId,
    } = this.props;

    if (typeof data === 'string') {
      const msg = JSON.parse(data);
      this.handlePeerMsg(msg);
    } else {
      this.recvBuffer.push(data);
      const curRecvBytes = this.recvSizes[curFileId] || 0;
      const newRecvBytes = curRecvBytes + data.byteLength;
      this.recvSizes[curFileId] = newRecvBytes;
      this.bps += data.byteLength;
      this.peer.sendJSON({
        type: 'chunkReceived',
        payload: {
          fileId: curFileId,
          recvBytes: newRecvBytes,
        },
      });
    }
  }

  handlePeerMsg(msg) {
    if (msg.type === 'fileStart') {
      this.props.setState({
        curFileId: msg.fileId,
      });
    } else if (msg.type === 'fileEnd') {
      const fileId = msg.fileId;
      const file = this.props.files.find(f => f.uid === fileId) || {};
      const blob = new Blob(this.recvBuffer, { type: file.type });
      this.recvBuffer = [];
      const url = window.URL.createObjectURL(blob);
      const files = this.props.files.map(f => {
        if (f.uid === fileId) {
          return {
            ...f,
            downloadUrl: url,
          };
        } else {
          return f;
        }
      });
      this.props.setState({
        files,
      });
    }
  }

  renderStep1() {
    const {
      recvCode,
    } = this.props;

    return (
      <>
        <Input placeholder="请输入6位收件码" inputClassName={styles.input} value={recvCode} onChange={this.onChangeRecvCode} />
        <Button type="primary" className={styles.recvBtn} onClick={this.onPrepareRecv}>接收文件</Button>
        <div className={styles.tip}>
          <Icon name="info" />
          如何获取收件码？
        </div>
      </>
    );
  }

  renderStep2() {
    const {
      peerState,
      started,
      curFileId,
      files,
    } = this.props;

    const totalBytes = files.reduce((sum, cur) => {
      return sum + cur.size;
    }, 0);

    let allCompleted = true;
    files.forEach(f => {
      if (!f.downloadUrl) {
        allCompleted = false;
      }
    });

    let btnContent = '开始下载';
    if (allCompleted) {
      btnContent = '继续接收';
    } else if (!started) {
      btnContent = '开始下载';
    } else if (peerState === 'disconnected' || peerState === 'connectFailed') {
      btnContent = '重新下载';
    } else if (peerState === 'connecting') {
      btnContent = '正在连接...';
    } else if (peerState === 'connected') {
      btnContent = '连接成功';
    } else if (peerState === 'transfer') {
      btnContent = `正在下载...(${prettyBytes(this.bps)}/s)`;
    }

    return (
      <>
        <div className={styles.msg1}>
          对方发送给您以下文件：
        </div>
        <FileBox files={this.props.files} curFileId={curFileId} />
        <div className={styles.msg2}>
          <div>{files.length} 个文件，共 {prettyBytes(totalBytes)}</div>
        </div>
        <Button
          type="primary"
          className={styles.recvBtn}
          loading={started && !allCompleted && (peerState === 'connected' || peerState === 'connecting' || peerState === 'transfer')}
          onClick={allCompleted ? this.onReset : this.onStartRecv}
        >
          {btnContent}
        </Button>
      </>
    );
  }

  render() {
    const {
      files,
    } = this.props;

    return (
      <div className={styles.base}>
        <div className={styles.titleRow}>
          {files.length > 0 && (
            <div className={styles.cancel} onClick={this.onReset}>
              取消
            </div>
          )}
          <div className={styles.title}>
            接收文件
          </div>
        </div>
        {files.length === 0 && this.renderStep1()}
        {files.length > 0 && this.renderStep2()}
      </div>
    );
  }
}

RecvFilePanel.propTypes = {
  recvCode: PropTypes.string,
  peerState: PropTypes.string,
  started: PropTypes.bool,
  files: PropTypes.array,
  targetId: PropTypes.string,
  curFileId: PropTypes.string,
  match: PropTypes.object,
  setState: PropTypes.func,
};

export default withRouter(RecvFilePanel);

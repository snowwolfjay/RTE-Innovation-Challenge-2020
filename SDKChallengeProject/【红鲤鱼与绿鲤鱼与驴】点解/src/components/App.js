import React from 'react';
import { connect } from 'react-redux';
import PropTypes from 'prop-types';
import ReactGA from 'react-ga';
import {
  withRouter,
} from 'react-router';
import {
  Switch,
  Route,
} from 'react-router-dom';
import * as Sentry from '@sentry/browser';

import Footer from '../components/Footer';
import HomePage from '../pages/HomePage';

import './App.css';
import './App.cm.styl';

class App extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      hasError: false,
    };
  }

  componentDidMount() {
    if (process.env.NODE_ENV === 'production') {
      this.props.history.listen(location => {
        ReactGA.pageview(location.pathname);
      });
    }
  }

  static getDerivedStateFromError() {
    return { hasError: true };
  }

  componentDidCatch(error, errorInfo) {
    Sentry.withScope(scope => {
      Object.keys(errorInfo).forEach(key => {
        scope.setExtra(key, errorInfo[key]);
      });
      Sentry.captureException(error);
    });
  }

  render() {
    const {
      hasError,
    } = this.state;

    if (hasError) {
      return (
        <>
          <h1 style={{ textAlign: 'center' }}>Server Error：<a href="">Refresh</a></h1>
        </>
      );
    }

    return (
      <Switch>
        <Route path="/">
          <HomePage />
          <Footer />
        </Route>
      </Switch>
    );
  }
}

App.propTypes = {
  sendHello: PropTypes.func,
  user: PropTypes.object,
  history: PropTypes.object,
};

function mapStateToProps(state) {
  return {
    user: state.user,
  };
}

export default withRouter(connect(mapStateToProps, {
})(App));

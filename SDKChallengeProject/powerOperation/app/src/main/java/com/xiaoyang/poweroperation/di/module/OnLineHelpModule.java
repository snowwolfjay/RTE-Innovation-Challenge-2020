package com.xiaoyang.poweroperation.di.module;

import com.jess.arms.di.scope.ActivityScope;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

import com.xiaoyang.poweroperation.mine.contract.OnLineHelpContract;
import com.xiaoyang.poweroperation.mine.model.OnLineHelpModel;


/**
 * ================================================
 * Description:
 * <p>
 * Created by MVPArmsTemplate on 08/12/2020 13:52
 * <a href="mailto:jess.yan.effort@gmail.com">Contact me</a>
 * <a href="https://github.com/JessYanCoding">Follow me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms">Star me</a>
 * <a href="https://github.com/JessYanCoding/MVPArms/wiki">See me</a>
 * <a href="https://github.com/JessYanCoding/MVPArmsTemplate">模版请保持更新</a>
 * ================================================
 */
@Module
public abstract class OnLineHelpModule {

    @Binds
    abstract OnLineHelpContract.Model bindOnLineHelpModel(OnLineHelpModel model);
}
package com.example.smallweb;

/**
 * 当没有可以回退的历史或前进的历史时的回调类
 */
public interface SendLoadingSignal {

	public void onCanBackOrGo(Boolean canBack, Boolean canGo);

	public void onStartLoading(Boolean yes);
}
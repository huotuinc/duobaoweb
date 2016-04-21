package com.huotu.mallduobao.service;

import com.huotu.mallduobao.model.admin.WebIssueSearchModel;
import com.huotu.mallduobao.model.admin.WebLotteryInfoModel;
import com.huotu.mallduobao.model.admin.WebPersonnalIssueListModel;
import com.huotu.mallduobao.model.admin.WebUserNumberModel;

import java.util.List;

/**
 * Created by lhx on 2016/4/1.
 */

public interface LotteryService {
    /**
     * 将期号page列表转换为List模型列表
     * @param webIssueSearchModel page列表
     * @return WebPersonnalIssueListModel
     */
    WebPersonnalIssueListModel getWebIssueListModel(WebIssueSearchModel webIssueSearchModel);


    /**
     * 后台获取中奖信息的单个模型
     * @param issueId
     * @return
     */
    WebLotteryInfoModel getWebLotteryInfoModel(Long issueId) throws Exception;

    /**
     * 获取某个用户某期的夺宝号码
     * @param userId 用户id
     * @param issueId 期号id
     * @return List<WebUserNumberModel>
     * @throws Exception
     */
    List<WebUserNumberModel> getMyRaiderNumbers(Long userId, Long issueId) throws Exception;

}

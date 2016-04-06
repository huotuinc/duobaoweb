/**
 * Created by xhk on 2016/3/28.
 */

//todo 加入必须的信息
var turnToBalance = function (cartId, buyNum, userId, issueId, customerId) {
    var cooOID = getCookie("qbdbopenid");
    var cooSIGN = getCookie("qbdbosign");

    var url = "balance?cartId=" + cartId +
        "&buyNum=" + buyNum +
        "&userId=" + userId +
        "&issueId=" + issueId +
        "&customerId=" + customerId +
        "&openId=" + cooOID +
        "&sign=" + cooSIGN;
    window.location.href = url;
}
//todo 加入必须的信息
var payToService = function (payMoney, detail, cartsId, payType, type, userId, issueId, customerId) {
    var cooOID = getCookie("qbdbopenid");
    var cooSIGN = getCookie("qbdbosign");

    var payModel = toPayModel(payMoney, detail, cartsId, payType, type);
    var url = "pay?userId=" + userId +
        "&issueId=" + issueId +
        "&customerId=" + customerId +
        "&openId=" + cooOID +
        "&sign=" + cooSIGN
    "&payModel=" + payModel;
    window.location.href = url;
}
function toPayModel(payMoney1, detail1, cartsId1, payType1, type1) {
    var obj = new Object();
    obj.payMoney = Number(payMoney1);
    obj.detail = String(detail1);
    obj.cartsId = Number(cartsId1);
    obj.payType = Number(payType1);
    obj.type = Number(type1);
    return obj;
}

var shoppingBuy = function (buyNum, issueId, customerId, userId) {
    var cooOID = getCookie("qbdbopenid");
    var cooSIGN = getCookie("qbdbosign");

    $.jBox.tip("正在结算...", "loading");
    $.ajax({
        url: "../shopping/joinToCarts",
        data: {
            issueId: issueId,
            buyNum: buyNum,
            userId: userId,
            customerId: customerId,
            openId: cooOID,
            sign: cooSIGN
        },
        type: "post",
        dataType: "json",
        success: function (data) {
            if (data.code == 200) {
                $.jBox.tip(data.message);
                window.location = "../shopping/showShoppingCarts?userId=" + userId +
                    "&issueId=" + issueId +
                    "&customerId=" + customerId +
                    "&openId=" + cooOID +
                    "&sign=" + cooSIGN;
            } else {
                $.jBox.tip(data.message);
            }
        },
        error: function (data) {
            $.jBox.tip("异常");
        }
    })
}

var shoppingAllBuy = function (issueId, customerId, userId) {
    var cooOID = getCookie("qbdbopenid");
    var cooSIGN = getCookie("qbdbosign");

    $.jBox.tip("正在结算...", "loading");
    $.ajax({
        url: "../shopping/allToCarts",
        data: {
            issueId: issueId,
            userId: userId,
            customerId: customerId,
            openId: cooOID,
            sign: cooSIGN
        },
        type: "post",
        dataType: "json",
        success: function (data) {
            if (data.code == 200) {
                $.jBox.tip(data.message);
                window.location = data.url;
            } else {
                $.jBox.tip(data.message);
            }
        },
        error: function (data) {
            $.jBox.tip("异常");
        }
    })
}
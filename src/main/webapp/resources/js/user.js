/**
 * Created by xhk on 2016/4/1.
 */

function getCookie(name) {
    var arr,reg=new RegExp("(^| )"+name+"=([^;]*)(;|$)");
    if(arr=document.cookie.match(reg))
        return unescape(arr[2]);
    else
        return null;
}
function setCookie(name,value) {
    var Days = 1;
    var exp = new Date();
    exp.setTime(exp.getTime() + Days*2*60*1000);
    document.cookie = name + "="+ escape (value) + ";expires=" + exp.toGMTString()+";path=/";
}
function DelCookie(name) {
    var exp = new Date();
    exp.setTime(exp.getTime() + (-1 * 24 * 60 * 60 * 1000));
    var cval = getCookie(name);
    document.cookie = name + "=" + cval + "; expires=" + exp.toGMTString();
}
var userInit=function(upCookie,userId,issueId,customerId,openidUrl,openId,sign){


    if(upCookie=='1')//如果标记为更新cookie则进行cookie的更新
    {
        alert("customerId"+customerId+";userId"+userId+";qbdbopenid:"+openId+";qbdbosign:"+sign);
        setCookie("qbdb"+customerId,userId);//进行cookie存储相应的商家和商家对应的用户
        setCookie("qbdbopenid",openId);
        setCookie("qbdbosign",sign);
    }

    var cooUID=getCookie("qbdb"+customerId);
    var cooOID=getCookie("qbdbopenid");
    var cooSIGN=getCookie("qbdbosign");
    alert(cooUID+";"+cooOID+";"+cooSIGN);
    if(cooUID==null||cooOID==null||cooSIGN==null){ //undefind
        //如果该用户的cookie为空则走服务器认证一遍openid
        var url = openidUrl;
        window.location.href=url;
    }else if(cooUID!=userId){
        //如果该用户的cookie与进入的网页的userid不一样则直接走cookie中的用户
        var url="../goods/detailByIssueId?userId="+cooUID+
            "&issueId="+issueId+
            "&customerId="+customerId+
            "&openId="+openId+
        "&sign="+sign+
            "&id="+issueId;
        alert(userId+url)
        window.location.href=url;
    }

}



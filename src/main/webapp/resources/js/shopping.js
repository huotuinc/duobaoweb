/**
 * Created by Administrator on 2016/3/28.
 */

var turnToBalance=function(cartId,buyNum){
    var url="balance?cartId="+cartId+"&buyNum="+buyNum;
    window.location.href=url;
}
/**
 * Created by xhk on 2016/3/28.
 */

var turnToBalance=function(cartId,buyNum){
    var url="balance?cartId="+cartId+"&buyNum="+buyNum;
    window.location.href=url;
}
var payToService=function(payMoney,detail,cartsId,payType,type){
    var payModel=toPayModel(payMoney,detail,cartsId,payType,type);
    var url="pay?payModel="+payModel;
    window.location.href=url;
}
function toPayModel(payMoney1,detail1,cartsId1,payType1,type1){
    var obj=new Object();
    obj.payMoney=Number(payMoney1);
    obj.detail=String(detail1);
    obj.cartsId=Number(cartsId1);
    obj.payType=Number(payType1);
    obj.type=Number(type1);
    return obj;
}

var shoppingBuy=function(buyNum,issueId,customerId,userId){

    $.jBox.tip("正在结算...", "loading");
    $.ajax({
        url:"../shopping/joinToCarts",
        data:{issueId:issueId,
            buyNum:buyNum,
            userId:userId,
            customerId:customerId
        },
        type:"post",
        dataType:"json",
        success:function(data){
            if(data.code==200) {
                $.jBox.tip(data.message);
                window.location = "../shopping/showShoppingCarts?userId="+userId+"&issueId="+issueId+"&customerId="+customerId;
            }else{
                $.jBox.tip(data.message);
            }
        },
        error:function(data){
            $.jBox.tip("异常");
        }
    })
}

var shoppingAllBuy=function(issueId,customerId,userId){
    $.jBox.tip("正在结算...", "loading");
    $.ajax({
        url:"../shopping/allToCarts",
        data:{issueId:issueId,
            userId:userId,
            customerId:customerId
        },
        type:"post",
        dataType:"json",
        success:function(data){
            if(data.code==200) {
                $.jBox.tip(data.message);
                window.location =data.url;
            }else{
                $.jBox.tip(data.message);
            }
        },
        error:function(data){
            $.jBox.tip("异常");
        }
    })
}
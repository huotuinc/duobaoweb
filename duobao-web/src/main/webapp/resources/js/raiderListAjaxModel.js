
$(function () {
    var data1 = { pageSize: 10 ,page: 1,lastFlag:0,issueId: $("#issueId").val(),customerId: $("#customerId").val()};
    var data2 = { type: $("#type").val(),pageSize: 10 ,page: 1,lastFlag:0,issueId: $("#issueId").val(),customerId: $("#customerId").val()};
    var url = "getMyInvolvedRecordAjax";
    var stemplete = "#detailTemplate";
    var snoneTemplete = "<div  style='background-color:#f5f5f5;'><div style='background-color:#f5f5f5;'> <div class='commfont' > <p class='juoo'><img src='../resources/images/db001.png' style='width:200px;'/></p><p class='ju tit_rem_big_s'>您还没有夺宝纪录</p> <p class='assa'><a href='javascript:history.go(-1);' class='tit_rem_big_s'> 返 回 </a> </p></div></div>";
    var data = data2;
    if($("#type").val()==3){
        url = "getMyLotteryListAjax"
        stemplete = "#LotteryTemplate";
        snoneTemplete = "<div  style='background-color:#f5f5f5;'><div style='background-color:#f5f5f5;'> <div class='commfont' > <p class='juoo'><img src='../resources/images/db001.png' style='width:200px;'/></p><p class='ju tit_rem_big_s'>您还没有中奖纪录</p> <p class='assa'><a href='javascript:history.go(-1);' class='tit_rem_big_s'> 返 回 </a> </p></div></div>";
        data=data1;
    }
    $("#content").Jload({
            url: url,
            method: "POST",
            msgImg: "../JLoad/img/loading_cart.gif",
            data:data,
            noneTemplete: snoneTemplete,// 没有数据模版
            isArtTemplete: true,
            Templete: $(stemplete).html()
        }
    );
})


var data = {pageSize: 10 ,page: 1, lastFlag:0,  issueId: $("#issueId").val(),customerId: $("#customerId").val()};
$(function () {
    var url = "getBuyListByIssueId";
    var stemplete = "#detailTemplate";
    var snoneTemplete = "";
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


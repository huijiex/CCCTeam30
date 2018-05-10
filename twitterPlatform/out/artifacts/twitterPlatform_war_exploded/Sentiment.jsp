<%--
  Created by IntelliJ IDEA.
  User: zsl
  Date: 2018/5/1
  Time: 14:00
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<link rel="stylesheet" href="css/bootstrap.min.css">
<link rel="stylesheet" href="css/bootstrap-table.css">
<link rel="stylesheet" href="css/myStyle.css">
<head>
    <title>Sentiment Analysis</title>

</head>
<body>
<nav nav class="navbar navbar-expand-sm bg-primary navbar-dark">
    <ul class="navbar-nav">
        <li class="nav-item">
            <a class="nav-link" href="index.jsp">Home</a>
        </li>
        <li class="nav-item active">
            <a class="nav-link" href="Sentiment.jsp">Twitter Analysis</a>
        </li>
        <li class="nav-item">
            <a class="nav-link" href="Map.jsp">Google Map</a>
        </li>
    </ul>

    <div class="dropdown" style="float:right">
        <button class="btn btn-primary dropdown-toggle" type="button" data-toggle="dropdown">Scenarios
            <span class="caret"></span></button>
        <ul class="dropdown-menu">
            <a id="to_food" class="dropdown-item">Food Topic</a>
            <a id="to_sent" class="dropdown-item">Sentiment Topic</a>
            <a id="to_arig" class="dropdown-item">Veggies Topic</a>
            <a id="to_female" class="dropdown-item">Beauty and Makeup Topic</a>
        </ul>
    </div>

</nav>

<div>
    <div style="height:50%;width:100%;">

        <div class="div-color" id="chart_container" style="height:100%;width:50%;float:left">
            <%--            <div style="width: 100%">
                            <button class="small blue button" id="left_btn" style="float: right">Load</button>
                        </div>--%>
            <canvas id="myChart" style="width:100%;height:90%;"></canvas>
        </div>

        <div class="div-color" id="chart_container2" style="height:100%; width:50%;float:right">
            <%--            <div style="width: 100%">
                            <button class="small blue button" id="right_btn" style="float: right">Load</button>
                        </div>--%>
            <canvas id="myChart2" style="width:100%;height:90%; "></canvas>
        </div>
    </div>
    <div class="div-color" style="height:40%;width:100%">
        <%--        <div style="width: 100%">
                    <button class="small blue button" id="table_btn" style="float: right">refresh table</button>
                </div >--%>
        <div style="width: 100% ;text-align: center">
            <legend id="tbl_title">Sentiment Analysis of Tweets in Each Suburb</legend>
        </div>
        <table id="sent_table"></table>
    </div>
</div>


<!-- Optional JavaScript -->
<!-- jQuery first, then Popper.js, then Bootstrap JS -->
<script src="js/jquery-3.3.1.min.js"></script>
<script src="js/popper.min.js"></script>
<script src="js/bootstrap.min.js"></script>
<script src="js/bootstrap-table.js"></script>
<script src="js/locale/bootstrap-table-en-US.js"></script>
<script src="js/Chart.bundle.min.js"></script>
<script src="js/palette.js"></script>

<script>
    var rankdata;
    var restaurantData;

    function readFoodRank(url,data) {
        var result
        $.ajax({
            async: false,
            type: "POST",
            url: url,
            dataType: "json",
            data:data,
            success: function (data) {
                console.log("food load success")
                result = data
            },
            error: function () {

            }
        });
        return result
    }



    function readRestRank() {
        $.ajax({
            async: false,
            type: "get",
            url: "/data/restaurant_rank.json",
            dataType: "json",
            success: function (data) {
                result = data
                console.log("restaurant load success")
            },
            error: function () {

            }
        });
        return result
    }

    function readSentWithOption(url,option) {
        var result;
        $.ajax({
            async: false,
            type: "post",
            url: url,
            dataType: "json",
            data: option,
            success: function (data) {
                result = data
            },
            error: function () {

            }
        });
        return result
    }

    var ctx = document.getElementById("myChart").getContext('2d');
    rankdata=readFoodRank("/getFoodData",{option:"food_chart"})
    console.log(rankdata.labels)
    var myChart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: rankdata.labels,
            datasets: [{
                label: 'Food Topic Rank ',
                data: rankdata.datasets[0].data,
                backgroundColor: palette('tol', rankdata.datasets[0].data.length).map(function (hex) {
                    return '#' + hex;
                })
            }]
        },
        options: {
            scales: {
                yAxes: [{
                    ticks: {
                        beginAtZero: true
                    }
                }]
            },
            title: {
                display: true,
                text: 'Food Topic Top 10 in Melb'
            }
            /*            color: function (ctx) {
                            var index = ctx.dataIndex;
                            var value = ctx.dataset.data[index];
                            return value < 0 ? 'red' :  // draw negative values in red
                                index % 2 ? 'blue' :    // else, alternate values in blue and green
                                    'green';
                        }*/
        }
    })

    var ctx2 = document.getElementById("myChart2").getContext('2d');
    restaurantData=readRestRank()
    var myChart2 = new Chart(ctx2, {
        type: 'pie',
        data: {
            labels: restaurantData.labels,
            datasets: [{
                label: restaurantData.datasets[0].label,
                data: restaurantData.datasets[0].data,
                backgroundColor: palette('tol-dv', restaurantData.datasets[0].data.length).map(function (hex) {
                    return '#' + hex;
                })
            }]
        },
        options: {
            scales: {
                yAxes: [{
                    ticks: {
                        beginAtZero: true
                    }
                }]
            },
            title: {
                display: true,
                text: restaurantData.datasets[0].label
            }
        }

    });

    $(document).ready(function () {
        initFoodTable()
        $("#tbl_title").text(function (i, text) {
            return 'Food Tweet Number in Each Suburb'
        })
    });

  function initFoodTable(){
      //先销毁表格
      $('#sent_table').bootstrapTable('destroy');
      //初始化表格,动态从服务器加载数据
      $("#sent_table").bootstrapTable({
          method: "POST",  //使用get请求到服务器获取数据
          url: "/getFoodData", //获取数据的Servlet地址,
          striped: true,  //表格显示条纹
          pagination: true, //启动分页
          pageSize: 5,  //每页显示的记录数
          pageList: [5, 10, 15, 20, 25],  //记录数可选列表
          search: false,  //是否启用查询
          sidePagination: "client", //表示服务端请求
          sortOrder: "desc",
          sortName: "count",
          columns: [{

              field: '_id',
              title: 'suburb'

          }, {
              field: "count",
              title: "count",
              sortable: true
          }
          ],
          //设置为undefined可以获取pageNumber，pageSize，searchText，sortName，sortOrder
          //设置为limit可以获取limit, offset, search, sort, order
          onLoadSuccess: function () {  //加载成功时执行
              console.log("table loading success");
          },
          onLoadError: function () {  //加载失败时执行
              console.log("table loading failed", {time: 1500, icon: 2});
          }
      });
  }

    function initSentTable() {
        //先销毁表格
        $('#sent_table').bootstrapTable('destroy');
        //初始化表格,动态从服务器加载数据
        $("#sent_table").bootstrapTable({
            method: "POST",  //使用get请求到服务器获取数据
            url: "/getSentData", //获取数据的Servlet地址
            striped: true,  //表格显示条纹
            pagination: true, //启动分页
            pageSize: 5,  //每页显示的记录数
            pageList: [5, 10, 15, 20, 25],  //记录数可选列表
            search: false,  //是否启用查询
            sidePagination: "client", //表示服务端请求
            sortOrder: "desc",
            sortName: "total_number",
            columns: [{

                field: '_id',
                title: 'suburb'

            }, {
                field: "positive_number",
                title: "positive",
                sortable: true
            }, {
                field: "negative_number",
                title: "negative",
                sortable: true
            }, {
                field: "total_number",
                title: "total",
                order: "desc",
                sortable: true
            }, {
                field: "positive_rate",
                title: "rate",
                sortable: true
            }
            ],
            //设置为undefined可以获取pageNumber，pageSize，searchText，sortName，sortOrder
            //设置为limit可以获取limit, offset, search, sort, order
            onLoadSuccess: function () {  //加载成功时执行
                console.log("table loading success");
            },
            onLoadError: function () {  //加载失败时执行
                console.log("table loading failed", {time: 1500, icon: 2});
            }
        });
    }

    function initCropTable() {
        //先销毁表格
        $('#sent_table').bootstrapTable('destroy');
        //初始化表格,动态从服务器加载数据
        $("#sent_table").bootstrapTable({
            method: "POST",  //使用get请求到服务器获取数据
            url: "/getCropData", //获取数据的Servlet地址
            striped: true,  //表格显示条纹
            pagination: true, //启动分页
            pageSize: 5,  //每页显示的记录数
            pageList: [5, 10, 15, 20, 25],  //记录数可选列表
            search: false,  //是否启用查询
            sidePagination: "client", //表示服务端请求
            sortOrder: "desc",
            sortName: "total_agriculture",
            columns: [{

                field: 'suburb',
                title: 'suburb'

            }, {
                field: "total_agriculture",
                title: "total agriculture",
                order: "desc",
                sortable: true
            }, {
                field: "total_value_of_crop",
                title: "total value of crop",
                order: "desc",
                sortable: true
            }, {
                field: "veggies_for_human",
                title: "total",
                order: "desc",
                sortable: true
            }
            ],
            //设置为undefined可以获取pageNumber，pageSize，searchText，sortName，sortOrder
            //设置为limit可以获取limit, offset, search, sort, order
            onLoadSuccess: function () {  //加载成功时执行
                console.log("crop table loading success");
            },
            onLoadError: function () {  //加载失败时执行
                console.log("crop table loading failed", {time: 1500, icon: 2});
            }
        });
    }

    function initFemaleTable() {
        //先销毁表格
        $('#sent_table').bootstrapTable('destroy');
        //初始化表格,动态从服务器加载数据
        $("#sent_table").bootstrapTable({
            method: "POST",  //使用get请求到服务器获取数据
            url: "/getFemaleData", //获取数据的Servlet地址
            striped: true,  //表格显示条纹
            pagination: true, //启动分页
            pageSize: 5,  //每页显示的记录数
            pageList: [5, 10, 15, 20, 25],  //记录数可选列表
            search: false,  //是否启用查询
            sidePagination: "client", //表示服务端请求
            sortOrder: "desc",
            sortName: "total_count",
            columns: [{

                field: 'suburb',
                title: 'suburb'

            }, {
                field: "count_15_19",
                title: "age 15-19",
                order: "desc",
                sortable: true
            }, {
                field: "count_20_24",
                title: "age 20-24",
                order: "desc",
                sortable: true
            }, {
                field: "count_25_29",
                title: "age 25-29",
                order: "desc",
                sortable: true
            }, {
                field: "count_30_34",
                title: "age 30-34",
                sortable: true
            }, {
                field: "count_35_39",
                title: "age 35-39",
                sortable: true
            }, {
                field: "total_count",
                title: "total count",
                sortable: true
            }

            ],
            //设置为undefined可以获取pageNumber，pageSize，searchText，sortName，sortOrder
            //设置为limit可以获取limit, offset, search, sort, order
            onLoadSuccess: function () {  //加载成功时执行
                console.log("female table loading success");
            },
            onLoadError: function () {  //加载失败时执行
                console.log("table loading failed", {time: 1500, icon: 2});
            }
        });
    }

    $("#table_btn").click(function () {
        var opt = {
            url: "/getGridData"
        };
        $("#sent_table").bootstrapTable('refresh', opt);

    });
    $("#to_food").click(function () {
        initFoodTable()
        initFoodCharts()
        $("#tbl_title").text(function (i, text) {
            return 'Food Tweet Number in Each Suburb'
        })
    })
    $("#to_arig").click(function () {
        initCropTable()
        initVeggiesCharts()
        $("#tbl_title").text(function (i, text) {
            return 'AURIN Crop Value in Each Suburb'
        })
    })
    $("#to_female").click(function () {
        initBeautyCharts()
        initFemaleTable()
        $("#tbl_title").text(function (i, text) {
            return 'AURIN Female Residents in Each Suburb'
        })
    })
    $("#to_sent").click(function () {
        initSentTable()
        $("#tbl_title").text(function (i, text) {
            return 'Sentiment Analysis of Tweets in Each Suburb'
        })
        initSentCharts()
    })

    function initSentCharts() {
        var option = {option: "sent_positive"}
        var result = readSentWithOption("/getSentData",option)
        var labels=new Array(10)
        var chart_data=new Array(10)
        for(i=0;i<labels.length;i++){
            labels[i]=result[i]._id
            chart_data[i]=result[i].positive_number
        }
        console.log(labels)
        console.log(chart_data)
        if (result != null) {
            var newData = {
                labels: labels,
                datasets: [{
                    label: 'Positive Tweets Rank',
                    data: chart_data,
                    backgroundColor: palette('tol', chart_data.length).map(function (hex) {
                        return '#' + hex;
                    })
                }]
            }
            myChart.data = newData
            myChart.options.title.text= 'Positive Tweet Number Top 10 in Melb'
            myChart.update()
        }
        var title='Total Tweet Number Top 10 in Melb'
        reLoadChart2("/getSentData",{option: "sent_total"},title);
    }

    function reLoadChart2(url,option,title){
        var result = readSentWithOption(url,option)
        var labels=new Array(10)
        var chart_data=new Array(10)
        if (option.option=="female_total"){
            for(i=0;i<labels.length;i++){
                labels[i]=result[i].suburb
                chart_data[i]=result[i].total_count
            }
        }
        else{
        for(i=0;i<labels.length;i++){
            labels[i]=result[i]._id
            chart_data[i]=result[i].total_number
        }
        }
        if (result != null) {
            var newData = {
                labels: labels,
                datasets: [{
                    label: 'Total Tweets Rank',
                    data: chart_data,
                    backgroundColor: palette('tol', chart_data.length).map(function (hex) {
                        return '#' + hex;
                    })
                }]
            }
            myChart2.data = newData
            myChart2.options.title.text= title
            myChart2.update()
        }
    }

    function initFoodCharts(){
        var result = readFoodRank("/getFoodData",{option:"food_chart"})
        if (result != null) {
            console.log(result.labels)
            console.log(result.datasets[0].data)
            var newData = {
                labels: result.labels,
                datasets: [{
                    label: 'Food Topic Top 10',
                    data: result.datasets[0].data,
                    backgroundColor: palette('tol', result.datasets[0].data.length).map(function (hex) {
                        return '#' + hex;
                    })
                }]
            }
            myChart.data = newData
            myChart.options.title.text= 'Food Related Tweets Top 10 in Melb'
            myChart.update()
        }

        reLoadRestRank()
    }

    function reLoadRestRank(){
        var result = readRestRank()
        if (result != null) {
            var newData = {
                labels: result.labels,
                datasets: [{
                    label: result.datasets[0].label,
                    data: result.datasets[0].data,
                    backgroundColor: palette('tol-dv', result.datasets[0].data.length).map(function (hex) {
                        return '#' + hex;
                    })
                }],
                options: {
                    title: {
                        display: true,
                        text: result.datasets[0].label
                    }
                }
            }
            myChart2.data = newData
            myChart2.options.title.text= 'Restaurant Number Top 10 in Melb'
            myChart2.update()
        }
    }

    function initVeggiesCharts(){
        var result = readFoodRank("/getVeggiesData",{option:"veggies_chart"})
        if (result != null) {
            console.log(result.labels)
            console.log(result.datasets[0].data)
            var newData = {
                labels: result.labels,
                datasets: [{
                    label: 'Veggies Topic Top 10',
                    data: result.datasets[0].data,
                    backgroundColor: palette('tol', result.datasets[0].data.length).map(function (hex) {
                        return '#' + hex;
                    })
                }]
            }
            myChart.data = newData
            myChart.options.title.text= ' Veggies Related Tweets Top 10 in Melb'
            myChart.update()
        }
        reLoadRestRank()
    }
    function initBeautyCharts(){

        var result = readFoodRank("/getVeggiesData",{option:"beauty_chart"})
        if (result != null) {
            console.log(result.labels)
            console.log(result.datasets[0].data)
            var newData = {
                labels: result.labels,
                datasets: [{
                    label: 'Beauty Makeup Topic Top 10',
                    data: result.datasets[0].data,
                    backgroundColor: palette('tol', result.datasets[0].data.length).map(function (hex) {
                        return '#' + hex;
                    })
                }]
            }
            myChart.data = newData
            myChart.options.title.text= ' Beauty Makeup Related Tweets Top 10 in Melb'
            myChart.update()
        }
        var title='Female Residents Number Top 10 in Melb'
        reLoadChart2("/getFemaleData",{option:"female_total"},title)
    }
</script>
</body>
</html>

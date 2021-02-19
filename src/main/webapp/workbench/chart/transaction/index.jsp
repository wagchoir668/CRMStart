<%
    String basePath = request.getScheme()+"://"+
            request.getServerName()+":"+request.getServerPort()+
            request.getContextPath()+"/";

//    根据交易表中的不同阶段，形成一个漏斗图（倒三角）
//    数量多的在上，数量少的在下
%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <base href="<%=basePath%>">
    <title>Title</title>
    <script src="ECharts/echarts.min.js"></script>
    <script src="jquery/jquery-1.11.1-min.js"></script>
    
    <script>
        $(function () {
           getCharts();
        })
        
        function getCharts() {
            //先取数据,每个阶段对应的交易tran的数量
            $.ajax({
                url:"workbench/transaction/getCharts.do",
                dataType:"json",
                type:"get",
                success:function (data) {
//data [total:x条,dataList:[{value:x条,stage:stage1},{value:y条,stage:stage2}]]
                    //把官网中的代码copy下来,漏斗图
                    var option = {
                        title: {
                            text: '交易漏斗图',
                            subtext: '统计交易阶段的数量'
                        },
                        toolbox: {
                            feature: {
                                dataView: {readOnly: false},
                                restore: {},
                                saveAsImage: {}
                            }
                        },
                        legend: {
                            data: ['展现','点击','访问','咨询','订单']
                        },

                        series: [
                            {
                                name:'交易漏斗图',
                                type:'funnel',
                                left: '10%',
                                top: 60,
                                //x2: 80,
                                bottom: 60,
                                width: '80%',
                                min: 0,
                                max: data.total,//总共有多少条
                                minSize: '0%',
                                maxSize: '100%',
                                sort: 'descending',
                                gap: 2,
                                label: {
                                    show: true,
                                    position: 'inside'
                                },
                                labelLine: {
                                    length: 10,
                                    lineStyle: {
                                        width: 1,
                                        type: 'solid'
                                    }
                                },
                                itemStyle: {
                                    borderColor: '#fff',
                                    borderWidth: 1
                                },
                                emphasis: {
                                    label: {
                                        fontSize: 20
                                    }
                                },
                                data: data.dataList//dataList是一个json数组
                            }
                        ]
                    };

                    //基于准备好的dom，初始化echarts实例
                    //这里的main为我们选择放入图表的标签
                    var myChart = echarts.init(document.getElementById('main'));

                    // 使用刚指定的配置项和数据显示图表
                    myChart.setOption(option);
                }
            })
        }
    </script>
</head>
<body>
    <!--为echarts准备一个600*400dom-->
    <div id="main" style="width: 600px;height: 400px;"></div>

</body>
</html>

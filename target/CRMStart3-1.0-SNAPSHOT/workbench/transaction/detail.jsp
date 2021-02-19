<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.List" %>
<%@ page import="com.wagchoir.crm.settings.domain.DictValue" %>
<%@ page import="com.wagchoir.crm.workbench.domain.Tran" %>
<%
String basePath = request.getScheme()+"://"+
request.getServerName()+":"+request.getServerPort()+
request.getContextPath()+"/";

//阶段对应的字典值
List<DictValue> stageList = (List)application.getAttribute("stage");
//阶段对应的可能性 key是阶段  value是可能性
Map<String,String> map = (Map)application.getAttribute("pMap");
Set<String> set = map.keySet();

int point = 0;//point即为正常阶段和丢失阶段的分界点
for(int i=0;i<stageList.size();i++)
{
	DictValue dv = stageList.get(i);
	String stage = dv.getValue();//阶段
	String possibility = map.get(stage);//可能性
	if("0".equals(possibility))
	{
		point = i;//point即为正常阶段和丢失阶段的分界点
		break;
	}
}
%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
	<base href="<%=basePath%>"/>
<meta charset="UTF-8">

<link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet" />

<style type="text/css">
.mystage{
	font-size: 20px;
	vertical-align: middle;
	cursor: pointer;
}
.closingDate{
	font-size : 15px;
	cursor: pointer;
	vertical-align: middle;
}
</style>
	
<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>

<script type="text/javascript">

	//默认情况下取消和保存按钮是隐藏的
	var cancelAndSaveBtnDefault = true;
	
	$(function(){
		$("#remark").focus(function(){
			if(cancelAndSaveBtnDefault){
				//设置remarkDiv的高度为130px
				$("#remarkDiv").css("height","130px");
				//显示
				$("#cancelAndSaveBtn").show("2000");
				cancelAndSaveBtnDefault = false;
			}
		});
		
		$("#cancelBtn").click(function(){
			//显示
			$("#cancelAndSaveBtn").hide();
			//设置remarkDiv的高度为130px
			$("#remarkDiv").css("height","90px");
			cancelAndSaveBtnDefault = true;
		});
		
		$(".remarkDiv").mouseover(function(){
			$(this).children("div").children("div").show();
		});
		
		$(".remarkDiv").mouseout(function(){
			$(this).children("div").children("div").hide();
		});
		
		$(".myHref").mouseover(function(){
			$(this).children("span").css("color","red");
		});
		
		$(".myHref").mouseout(function(){
			$(this).children("span").css("color","#E6E6E6");
		});

		//阶段提示框
		$(".mystage").popover({
            trigger:'manual',
            placement : 'bottom',
            html: 'true',
            animation: false
        }).on("mouseenter", function () {
                    var _this = this;
                    $(this).popover("show");
                    $(this).siblings(".popover").on("mouseleave", function () {
                        $(_this).popover('hide');
                    });
                }).on("mouseleave", function () {
                    var _this = this;
                    setTimeout(function () {
                        if (!$(".popover:hover").length) {
                            $(_this).popover("hide")
                        }
                    }, 100);
                });

		showHistoryList();
		//页面全部加载完毕后
	});

	//显示刷新/阶段历史
	function showHistoryList() {
		$.ajax({
			url:"workbench/transaction/getHistory.do",
			data:{
				"tranId":"${tran.id}"
			},
			dataType:"json",
			type:"get",
			success:function (data) {
				// data [{history1},{history2},{history3}]
				var html = "";
				$.each(data,function (i,n) {
					html += '<tr>';
					html += '<td>'+n.stage+'</td>';
					html += '<td>'+n.money+'</td>';
					html += '<td>'+n.possibility+'</td>';
					html += '<td>'+n.expectedDate+'</td>';
					html += '<td>'+n.createTime+'</td>';
					html += '<td>'+n.createBy+'</td>';
					html += '</tr>';
				})

				$("#historyBody").html(html);
			}
		})
	}

	//每个阶段对应的图标，一点击该图标就触发此函数，改变原交易记录的阶段  i为阶段对应的下标
    //对原记录进行修改，同时对页面图标进行修改,再生成一条新的交易历史
	function changeStage(stage,i) {
		if(confirm("确定更改交易阶段吗？"))
		{
			$.ajax({
				url:"workbench/transaction/changeStage.do",
				data: {
					"id":"${tran.id}",
					"stage":stage,
					"money":"${tran.money}",//用来生成交易历史
					"expectedDate":"${tran.expectedDate}"
				},
				dataType: "json",
				type: "post",
				success:function (data) {
					// data {success:true/false,tran:tran}
					if(data.success)
					{
						//刷新页面  图标/阶段/可能性/修改者/修改时间
						$("#stage").html(data.tran.stage);
						$("#possibility").html(data.tran.possibility);
						$("#editBy").html(data.tran.editBy);
						$("#editTime").html(data.tran.editTime);

						//改变图标
						changeIcon(stage,i);

						//刷新阶段历史
						showHistoryList();
						alert("更改阶段成功！");
					}
					else{
						alert("抱歉，改变阶段失败");
					}
				}
			})
		}
	}

	function changeIcon(stage,index) {
		var currentStage = stage;//当前阶段
		var currentPossibility = $("#possibility").html();//当前阶段对应的可能性
		var index = index;//当前阶段下标
		var point = "<%=point%>";//正常阶段和丢失阶段的分界点

		//如果当前阶段可能性为0,前7个为黑圈，后2个中一个是黑叉一个是红叉
		if(currentPossibility=="0")
		{
			for(var i=0;i<point;i++)
			{
				//前7个,黑圈
				$("#"+i).removeClass();//移除掉原有的样式
				//添加新样式
				$("#"+i).addClass("glyphicon glyphicon-record mystage");
				$("#"+i).css("color","#000000");//为样式赋予颜色
			}
			for(var i=point;i<<%=stageList.size()%>;i++)
			{
				//后2个
				if(i==index)
				{
					//当前阶段，红叉
					$("#"+i).removeClass();//移除掉原有的样式
					//添加新样式
					$("#"+i).addClass("glyphicon glyphicon-remove mystage");
					$("#"+i).css("color","#FF0000");//为样式赋予颜色
				}else{
					//黑叉
					$("#"+i).removeClass();//移除掉原有的样式
					//添加新样式
					$("#"+i).addClass("glyphicon glyphicon-remove mystage");
					$("#"+i).css("color","#000000");//为样式赋予颜色
				}
			}
		}
		else{//如果当前阶段可能性不为0，前7个有可能是绿圈/绿色标记/黑圈，后2个一定是黑叉
			for(var i=0;i<point;i++)
			{
				//前7个
				if(i==index)
				{
					//绿色标记
					$("#"+i).removeClass();//移除掉原有的样式
					//添加新样式
					$("#"+i).addClass("glyphicon glyphicon-map-marker mystage");
					$("#"+i).css("color","#90F790");//为样式赋予颜色
				}else if(i<index)
				{
					//绿圈
					$("#"+i).removeClass();//移除掉原有的样式
					//添加新样式
					$("#"+i).addClass("glyphicon glyphicon-ok-circle mystage");
					$("#"+i).css("color","#90F790");//为样式赋予颜色
				}else{
					//黑圈
					$("#"+i).removeClass();//移除掉原有的样式
					//添加新样式
					$("#"+i).addClass("glyphicon glyphicon-record mystage");
					$("#"+i).css("color","#000000");//为样式赋予颜色
				}
			}
			for(var i=point;i<<%=stageList.size()%>;i++)
			{
				//后2个，黑叉
				$("#"+i).removeClass();//移除掉原有的样式
				//添加新样式
				$("#"+i).addClass("glyphicon glyphicon-remove mystage");
				$("#"+i).css("color","#000000");//为样式赋予颜色
			}
		}
	}
	
</script>

</head>
<body>
	
	<!-- 返回按钮 -->
	<div style="position: relative; top: 35px; left: 10px;">
		<a href="javascript:void(0);" onclick="window.history.back();"><span class="glyphicon glyphicon-arrow-left" style="font-size: 20px; color: #DDDDDD"></span></a>
	</div>
	
	<!-- 大标题 -->
	<div style="position: relative; left: 40px; top: -30px;">
		<div class="page-header">
			<h3>${tran.customerId}-${tran.name}<small>￥${tran.money}</small></h3>
		</div>
		<div style="position: relative; height: 50px; width: 250px;  top: -72px; left: 700px;">
			<button type="button" class="btn btn-default" onclick="window.location.href='edit.html';"><span class="glyphicon glyphicon-edit"></span> 编辑</button>
			<button type="button" class="btn btn-danger"><span class="glyphicon glyphicon-minus"></span> 删除</button>
		</div>
	</div>

	<!-- 阶段状态 -->
	<div style="position: relative; left: 40px; top: -50px;">
		阶段&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<%
			Tran tran = (Tran)request.getAttribute("tran");
			String currentStage = tran.getStage();//当前阶段
			String currentPossibility = map.get(currentStage);//当前阶段对应的可能性
			//如果当前阶段可能性为0,前7个为黑圈，后2个中一个是黑叉一个是红叉
			if("0".equals(currentPossibility))
			{
				for(int i=0;i<stageList.size();i++)
				{
					DictValue dictValue = stageList.get(i);
					String stage = dictValue.getValue();
					String possibility = map.get(stage);
					if("0".equals(possibility))//若遍历出来的可能性为0，说明是后两个
					{
						if(stage.equals(currentStage))//如果是当前阶段
						{
							//红叉
		%>
		<span id="<%=i%>" onclick="changeStage('<%=stage%>','<%=i%>')"
			  class="glyphicon glyphicon-remove mystage"
			  data-toggle="popover" data-placement="bottom"
			  data-content="<%=dictValue.getText()%>"
			  style="color: #FF0000;"></span>
		---------
		<%
						}else
			//黑叉
		{
		%>

		<span id="<%=i%>" onclick="changeStage('<%=stage%>','<%=i%>')"
		class="glyphicon glyphicon-remove mystage"
		data-toggle="popover" data-placement="bottom"
		data-content="<%=dictValue.getText()%>"
		style="color: #000000;"></span>
		---------
		<%
						}
					}
					else//若遍历出来的可能性不为0，说明是前7个，是黑圈
					{
		%>
		<span id="<%=i%>" onclick="changeStage('<%=stage%>','<%=i%>')"
			  class="glyphicon glyphicon-record mystage"
			  data-toggle="popover" data-placement="bottom"
			  data-content="<%=dictValue.getText()%>"
			  style="color: #000000;"></span>
		---------
		<%
					}
				}
			}
			else{//如果当前阶段可能性不为0，前7个有可能是绿圈/绿色标记/黑圈，后2个一定是黑叉
				int index = 0;//拿到当前阶段的下标
				for(int i=0;i<stageList.size();i++)
				{
					DictValue dictValue = stageList.get(i);
					String stage = dictValue.getValue();
					if(stage.equals(currentStage))
					{
						index = i;
						break;
					}
				}

				for(int i=0;i<stageList.size();i++)
				{
					DictValue dictValue = stageList.get(i);
					String stage = dictValue.getValue();
					String possibility = map.get(stage);

					if("0".equals(possibility))//如果可能性为0，一定是后两个阶段，是黑叉
					{
						//黑叉
						%>
        <span id="<%=i%>" onclick="changeStage('<%=stage%>','<%=i%>')"
              class="glyphicon glyphicon-remove mystage"
              data-toggle="popover" data-placement="bottom"
              data-content="<%=dictValue.getText()%>"
              style="color: #000000;"></span>
        ---------
		<%
					}
					else{//如果可能性不为0，一定是前七个阶段，绿圈/绿色标记/黑圈
						//如果是当前阶段，绿色标记
						if(i==index)
						{
        %>
        <span id="<%=i%>" onclick="changeStage('<%=stage%>','<%=i%>')"
              class="glyphicon glyphicon-map-marker mystage"
              data-toggle="popover" data-placement="bottom"
              data-content="<%=dictValue.getText()%>"
              style="color: #90F790;"></span>
        ---------
        <%

						}else if(i<index)//小于当前阶段，绿圈
						{
        %>
		<span id="<%=i%>" onclick="changeStage('<%=stage%>','<%=i%>')"
			  class="glyphicon glyphicon-ok-circle mystage"
			  data-toggle="popover" data-placement="bottom"
			  data-content="<%=dictValue.getText()%>"
			  style="color: #90F790;"></span>
		---------

        <%
						}else{
							//大于当前阶段，黑圈
		%>

		<span id="<%=i%>" onclick="changeStage('<%=stage%>','<%=i%>')"
			  class="glyphicon glyphicon-record mystage"
			  data-toggle="popover" data-placement="bottom"
			  data-content="<%=dictValue.getText()%>"
			  style="color: #000000;"></span>
		---------
		<%
						}
					}
				}
			}
		%>
		<span class="closingDate">${tran.expectedDate}</span>
	</div>
	
	<!-- 详细信息 -->
	<div style="position: relative; top: 0px;">
		<div style="position: relative; left: 40px; height: 30px;">
			<div style="width: 300px; color: gray;">所有者</div>
			<div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${tran.owner}</b></div>
			<div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">金额</div>
			<div style="width: 300px;position: relative; left: 650px; top: -60px;"><b>${tran.money}元</b></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 10px;">
			<div style="width: 300px; color: gray;">名称</div>
			<div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${tran.name}</b></div>
			<div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">预计成交日期</div>
			<div style="width: 300px;position: relative; left: 650px; top: -60px;"><b>${tran.expectedDate}</b></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 20px;">
			<div style="width: 300px; color: gray;">客户名称</div>
			<div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${tran.customerId}</b></div>
			<div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">阶段</div>
			<div style="width: 300px;position: relative; left: 650px; top: -60px;"><b id="stage">${tran.stage}</b></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 30px;">
			<div style="width: 300px; color: gray;">类型</div>
			<div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${tran.type}</b></div>
			<div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">可能性</div>
			<div style="width: 300px;position: relative; left: 650px; top: -60px;"><b id="possibility">${tran.possibility}</b></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 40px;">
			<div style="width: 300px; color: gray;">来源</div>
			<div style="width: 300px;position: relative; left: 200px; top: -20px;"><b>${tran.source}</b></div>
			<div style="width: 300px;position: relative; left: 450px; top: -40px; color: gray;">市场活动源</div>
			<div style="width: 300px;position: relative; left: 650px; top: -60px;"><b>${tran.activityId}</b></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px;"></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -60px; left: 450px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 50px;">
			<div style="width: 300px; color: gray;">联系人名称</div>
			<div style="width: 500px;position: relative; left: 200px; top: -20px;"><b>${tran.contactsId}</b></div>
			<div style="height: 1px; width: 550px; background: #D5D5D5; position: relative; top: -20px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 60px;">
			<div style="width: 300px; color: gray;">创建者</div>
			<div style="width: 500px;position: relative; left: 200px; top: -20px;"><b>${tran.createBy}&nbsp;&nbsp;</b><small style="font-size: 10px; color: gray;">${tran.createTime}</small></div>
			<div style="height: 1px; width: 550px; background: #D5D5D5; position: relative; top: -20px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 70px;">
			<div style="width: 300px; color: gray;">修改者</div>
			<div style="width: 500px;position: relative; left: 200px; top: -20px;"><b id="editBy">${tran.editBy}&nbsp;&nbsp;</b><small style="font-size: 10px; color: gray;" id="editTime">${tran.editTime}</small></div>
			<div style="height: 1px; width: 550px; background: #D5D5D5; position: relative; top: -20px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 80px;">
			<div style="width: 300px; color: gray;">描述</div>
			<div style="width: 630px;position: relative; left: 200px; top: -20px;">
				<b>
					${tran.description}
				</b>
			</div>
			<div style="height: 1px; width: 850px; background: #D5D5D5; position: relative; top: -20px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 90px;">
			<div style="width: 300px; color: gray;">联系纪要</div>
			<div style="width: 630px;position: relative; left: 200px; top: -20px;">
				<b>
					&nbsp;${tran.contactSummary}
				</b>
			</div>
			<div style="height: 1px; width: 850px; background: #D5D5D5; position: relative; top: -20px;"></div>
		</div>
		<div style="position: relative; left: 40px; height: 30px; top: 100px;">
			<div style="width: 300px; color: gray;">下次联系时间</div>
			<div style="width: 500px;position: relative; left: 200px; top: -20px;"><b>&nbsp;${tran.nextContactTime}</b></div>
			<div style="height: 1px; width: 400px; background: #D5D5D5; position: relative; top: -20px;"></div>
		</div>
	</div>
	
	<!-- 备注 -->
	<div style="position: relative; top: 100px; left: 40px;">
		<div class="page-header">
			<h4>备注</h4>
		</div>
		
		<!-- 备注1 -->
		<div class="remarkDiv" style="height: 60px;">
			<img title="zhangsan" src="image/user-thumbnail.png" style="width: 30px; height:30px;">
			<div style="position: relative; top: -40px; left: 40px;" >
				<h5>哎呦！</h5>
				<font color="gray">交易</font> <font color="gray">-</font> <b>动力节点-交易01</b> <small style="color: gray;"> 2017-01-22 10:10:10 由zhangsan</small>
				<div style="position: relative; left: 500px; top: -30px; height: 30px; width: 100px; display: none;">
					<a class="myHref" href="javascript:void(0);"><span class="glyphicon glyphicon-edit" style="font-size: 20px; color: #E6E6E6;"></span></a>
					&nbsp;&nbsp;&nbsp;&nbsp;
					<a class="myHref" href="javascript:void(0);"><span class="glyphicon glyphicon-remove" style="font-size: 20px; color: #E6E6E6;"></span></a>
				</div>
			</div>
		</div>
		
		<!-- 备注2 -->
		<div class="remarkDiv" style="height: 60px;">
			<img title="zhangsan" src="image/user-thumbnail.png" style="width: 30px; height:30px;">
			<div style="position: relative; top: -40px; left: 40px;" >
				<h5>呵呵！</h5>
				<font color="gray">交易</font> <font color="gray">-</font> <b>动力节点-交易01</b> <small style="color: gray;"> 2017-01-22 10:20:10 由zhangsan</small>
				<div style="position: relative; left: 500px; top: -30px; height: 30px; width: 100px; display: none;">
					<a class="myHref" href="javascript:void(0);"><span class="glyphicon glyphicon-edit" style="font-size: 20px; color: #E6E6E6;"></span></a>
					&nbsp;&nbsp;&nbsp;&nbsp;
					<a class="myHref" href="javascript:void(0);"><span class="glyphicon glyphicon-remove" style="font-size: 20px; color: #E6E6E6;"></span></a>
				</div>
			</div>
		</div>
		
		<div id="remarkDiv" style="background-color: #E6E6E6; width: 870px; height: 90px;">
			<form role="form" style="position: relative;top: 10px; left: 10px;">
				<textarea id="remark" class="form-control" style="width: 850px; resize : none;" rows="2"  placeholder="添加备注..."></textarea>
				<p id="cancelAndSaveBtn" style="position: relative;left: 737px; top: 10px; display: none;">
					<button id="cancelBtn" type="button" class="btn btn-default">取消</button>
					<button type="button" class="btn btn-primary">保存</button>
				</p>
			</form>
		</div>
	</div>
	
	<!-- 阶段历史 -->
	<div>
		<div style="position: relative; top: 100px; left: 40px;">
			<div class="page-header">
				<h4>阶段历史</h4>
			</div>
			<div style="position: relative;top: 0px;">
				<table id="activityTable" class="table table-hover" style="width: 900px;">
					<thead>
						<tr style="color: #B3B3B3;">
							<td>阶段</td>
							<td>金额</td>
							<td>可能性</td>
							<td>预计成交日期</td>
							<td>创建时间</td>
							<td>创建人</td>
						</tr>
					</thead>
					<tbody id="historyBody">
					</tbody>
				</table>
			</div>
			
		</div>
	</div>
	
	<div style="height: 200px;"></div>
	
</body>
</html>
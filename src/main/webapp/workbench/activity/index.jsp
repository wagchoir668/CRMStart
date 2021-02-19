<%
String basePath = request.getScheme()+"://"+
request.getServerName()+":"+request.getServerPort()+
request.getContextPath()+"/";
%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html>
<head>
	<base href="<%=basePath%>"/>
<meta charset="UTF-8">

<link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet" />
<link href="jquery/bootstrap-datetimepicker-master/css/bootstrap-datetimepicker.min.css" type="text/css" rel="stylesheet" />

<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/js/bootstrap-datetimepicker.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/locale/bootstrap-datetimepicker.zh-CN.js"></script>

<link rel="stylesheet" type="text/css" href="jquery/bs_pagination/jquery.bs_pagination.min.css">
<script type="text/javascript" src="jquery/bs_pagination/en.js"></script>
<script type="text/javascript" src="jquery/bs_pagination/jquery.bs_pagination.min.js"></script>

<script type="text/javascript">

	$(function(){
		$("#addBtn").click(function () {
			$.ajax({
						url:"workbench/activity/getUserList.do",
						dataType:"json",
						success:function (data) {
							var html = "<option></option>";

							$.each(data,function (i,n) {
								html += "<option value='"+n.id+"'>"+n.name+"</option>";
							})

							$("#create-owner").html(html);
							//在js中写el表达式，必须要带“”
							$("#create-owner").val("${sessionScope.user.id}");

							//从文件夹中复制而来
							$(".time").datetimepicker({
								minView: "month",
								language:  'zh-CN',
								format: 'yyyy-mm-dd',
								autoclose: true,
								todayBtn: true,
								pickerPosition: "bottom-left"
							});

							$("#createActivityModal").modal("show");
						}
			}
			)
		})

		//添加操作
		$("#saveBtn").click(function () {
			$.ajax({
				url:"workbench/activity/save.do",
				data:{
					"name":$.trim($("#create-name").val()),
					"owner":$.trim($("#create-owner").val()),
					"startDate":$.trim($("#create-startDate").val()),
					"endDate":$.trim($("#create-endDate").val()),
					"cost":$.trim($("#create-cost").val()),
					"description":$.trim($("#create-description").val()),
				},
				type:"post",
				dataType: "json",
				success:function(data) {
					// data   {success:true/false}
					if(data.success)
					{
						alert("创建新活动成功!")
						//先清空模态窗口中填写的内容
						$("#activityAddForm")[0].reset();
						//再关闭模态窗口
						$("#createActivityModal").modal("hide");

						//添加后刷新列表 pageList函数 第一个参数：跳转到第几页 第二个参数：每页多少条记录
						//操作后停留在当前页:
						// $("#activityPage").bs_pagination('getOption','currentPage')
						//维持目前每页展现的记录数:
						//$("#activityPage").bs_pagination('getOption','rowsPerPage')
						pageList(1,$("#activityPage").bs_pagination('getOption','rowsPerPage'));
					}
					else{
						alert("创建新市场活动失败");
					}
				}
			})
		})

		pageList(1,3);//页面全部加载完毕后，刷新下市场活动列表

		//查询
		$("#searchBtn").click(function () {
			$("#hidden-name").val($.trim($("#search-name").val()));
			$("#hidden-owner").val($.trim($("#search-owner").val()));
			$("#hidden-startDate").val($.trim($("#search-startDate").val()));
			$("#hidden-endDate").val($.trim($("#search-endDate").val()));

			//查询后刷新列表
			pageList(1,$("#activityPage").bs_pagination('getOption','rowsPerPage'));
		})

		//全选的复选框
		$("#activityQXBox").click(function () {
			$(".activityCheckBox").prop("checked",this.checked);
		})

		//下面的框全被选中，复选框则也选中,经典代码
		$("#activityBody").on("click",$(".activityCheckBox"),function () {
			$("#activityQXBox").prop("checked",
					$(".activityCheckBox:checked").length==$(".activityCheckBox").length)
		})

		//删除按钮，删除某条市场活动记录
		$("#deleteBtn").click(function () {
			var $xz = $(".activityCheckBox:checked");
			if($xz.length==0)
			{
				alert("请选择要删除的市场活动记录");
			}
			else{
				if(confirm("确定删除所选中的记录吗？"))
				{
					//由于一个id对应多个value（删多条），因此用json会较麻烦，因此使用拼接参数
					//使用key=value&key=value形式
					var param = "";
					for(var i=0;i<$xz.length;i++)
					{
						param += "id="+$($xz[i]).val();
						if(i<$xz.length-1)
						{
							param += "&";
						}
					}

					$.ajax({
						url:"workbench/activity/delete.do",
						data:param,
						dataType:"json",
						type:"post",
						success:function (data) {
							// data {success:true/false}
							if(data.success)
							{
								alert("删除记录成功！");
								pageList(1,$("#activityPage").bs_pagination('getOption','rowsPerPage'));
							}else{
								alert("抱歉，删除记录失败，请稍后再试");
							}
						}
					})
				}
			}
		})

		//修改之前，先根据id拿到这条记录的具体内容
		$("#editBtn").click(function () {
			var $xz = $(".activityCheckBox:checked");
			if($xz.length==0)
			{
				alert("请选中一条记录以修改");
			}else if($xz.length>1)
			{
				alert("只能选择一条记录");
			}else{
				var id = $xz.val();
				$.ajax({
					url:"workbench/activity/getUserListAndActivity.do",
					data:{"id":id},
					dataType:"json",
					type:"get",
					success:function (data) {
						//根据id去DB中取到对应的activity记录+所有的User的name
						// data: {uList:List<User>,activi:activity}
						var html = "<option></option>";
						$.each(data.uList,function (i,n) {
							html += "<option value='"+n.id+"'>"+n.name+"</option>";
						})
						$("#edit-owner").html(html);
						$("#edit-owner").val(data.activi.owner);
						$("#edit-name").val(data.activi.name);
						$("#edit-startDate").val(data.activi.startDate);
						$("#edit-endDate").val(data.activi.endDate);
						$("#edit-cost").val(data.activi.cost);
						$("#edit-description").val(data.activi.description);
						$("#edit-id").val(data.activi.id);

						$("#editActivityModal").modal("show");//打开模态窗口
					}
				})
			}
		})

		//修改
		$("#updateBtn").click(function () {
			$.ajax({
				url:"workbench/activity/update.do",
				data:{
					"id":$.trim($("#edit-id").val()),
					"name":$.trim($("#edit-name").val()),
					"owner":$.trim($("#edit-owner").val()),
					"startDate":$.trim($("#edit-startDate").val()),
					"endDate":$.trim($("#edit-endDate").val()),
					"cost":$.trim($("#edit-cost").val()),
					"description":$.trim($("#edit-description").val()),
				},
				type:"post",
				dataType: "json",
				success:function(data) {
					// data   {success:true/false}
					if(data.success)
					{
						alert("修改活动成功!")
						//修改后，刷新列表，再关闭模态窗口
						pageList($("#activityPage").bs_pagination('getOption','currentPage'),
								$("#activityPage").bs_pagination('getOption','rowsPerPage'));
						$("#editActivityModal").modal("hide");
					}
					else{
						alert("抱歉，修改市场活动失败");
					}
				}
			})
		})
		//页面全部加载完毕后调用
	});

	//什么情况下调用：1点左侧市场活动  2增删改查 3分页
	function pageList(pageNo,pageSize) {
		//在每次刷新页面前，干掉复选框中的勾
		$("#activityQXBox").prop("checked",false);

		$("#search-name").val($.trim($("#hidden-name").val()));
		$("#search-owner").val($.trim($("#hidden-owner").val()));
		$("#search-startDate").val($.trim($("#hidden-startDate").val()));
		$("#search-endDate").val($.trim($("#hidden-endDate").val()));

		$.ajax({
			url:"workbench/activity/pageList.do",
			data:{
				"pageNo":pageNo,
				"pageSize":pageSize,//分页查询
				"name":$.trim($("#search-name").val()),//条件查询
				"endDate":$.trim($("#search-endDate").val()),
				"startDate":$.trim($("#search-startDate").val()),
				"owner":$.trim($("#search-owner").val()),
			},
			dataType:"json",
			type:"get",
			success:function (data) {
				//data {"total总记录条数":"10","dataList":[{activity1},{activity2},{activity3}]}
				var html = "";
				$.each(data.dataList,function (i,n) {
					html += '<tr class="active">';
					html += '<td><input type="checkbox" class="activityCheckBox" value="'+n.id+'"/></td>';
					html += '<td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href=\'workbench/activity/detail.do?id='+n.id+'\';">'+n.name+'</a></td>';
					html += '<td>'+n.owner+'</td>';
					html += '<td>'+n.startDate+'</td>';
					html += '<td>'+n.endDate+'</td>';
					html += '</tr>';
				})

				$("#activityBody").html(html);

				var totalPages = data.total%pageSize==0?data.total/pageSize:parseInt(data.total/pageSize)+1;

				//分页显示，使用分页插件
				$("#activityPage").bs_pagination({
					currentPage: pageNo,//页码
					rowsPerPage: pageSize,//每页显示的记录条数
					maxRowsPerPage: 20,//每页最多显示的记录条数
					totalPages: totalPages,//总页数
					totalRows: data.total,//总记录条数

					visiblePageLinks: 5,//显示几个卡片，即为上一页下一页中的12345

					showGoToPage: true,
					showRowsPerPage: true,
					showRowsInfo: true,
					showRowsDefaultInfo: true,

					//回调函数，点击分页组件时触发
					onChangePage : function(event,data) {
						pageList(data.currentPage,data.rowsPerPage);
					},
				});
			}
		})
	}
</script>
</head>
<body>

	<input type="hidden" id="hidden-name"/>
	<input type="hidden" id="hidden-owner"/>
	<input type="hidden" id="hidden-startDate"/>
	<input type="hidden" id="hidden-endDate"/>

	<!-- 创建市场活动的模态窗口 -->
	<div class="modal fade" id="createActivityModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 85%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel1">创建市场活动</h4>
				</div>
				<div class="modal-body">
				
					<form id="activityAddForm" class="form-horizontal" role="form">
					
						<div class="form-group">
							<label for="create-marketActivityOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="create-owner">
								</select>
							</div>
                            <label for="create-marketActivityName" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="create-name">
                            </div>
						</div>
						
						<div class="form-group">
							<label for="create-startTime" class="col-sm-2 control-label">开始日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="create-startDate" readonly>
							</div>
							<label for="create-endTime" class="col-sm-2 control-label">结束日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control  time" id="create-endDate" readonly>
							</div>
						</div>
                        <div class="form-group">

                            <label for="create-cost" class="col-sm-2 control-label">成本</label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="create-cost">
                            </div>
                        </div>
						<div class="form-group">
							<label for="create-describe" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<textarea class="form-control" rows="3" id="create-description"></textarea>
							</div>
						</div>
						
					</form>
					
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" id="saveBtn">保存</button>
				</div>
			</div>
		</div>
	</div>
	
	<!-- 修改市场活动的模态窗口 -->
	<div class="modal fade" id="editActivityModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 85%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel2">修改市场活动</h4>
				</div>
				<div class="modal-body">
				
					<form class="form-horizontal" role="form">

						<input type="hidden" id="edit-id"/>
					
						<div class="form-group">
							<label for="edit-marketActivityOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="edit-owner">

								</select>
							</div>
                            <label for="edit-marketActivityName" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="edit-name">
                            </div>
						</div>

						<div class="form-group">
							<label for="edit-startTime" class="col-sm-2 control-label">开始日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="edit-startDate">
							</div>
							<label for="edit-endTime" class="col-sm-2 control-label">结束日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control time" id="edit-endDate">
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-cost" class="col-sm-2 control-label">成本</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-cost">
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-describe" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<textarea class="form-control" rows="3" id="edit-description"></textarea>
							</div>
						</div>
						
					</form>
					
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" id="updateBtn">更新</button>
				</div>
			</div>
		</div>
	</div>
	
	<div>
		<div style="position: relative; left: 10px; top: -10px;">
			<div class="page-header">
				<h3>市场活动列表</h3>
			</div>
		</div>
	</div>
	<div style="position: relative; top: -20px; left: 0px; width: 100%; height: 100%;">
		<div style="width: 100%; position: absolute;top: 5px; left: 10px;">
		
			<div class="btn-toolbar" role="toolbar" style="height: 80px;">
				<form class="form-inline" role="form" style="position: relative;top: 8%; left: 5px;">
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">名称</div>
				      <input class="form-control" type="text" id="search-name">
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">所有者</div>
				      <input class="form-control" type="text" id="search-owner">
				    </div>
				  </div>


				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">开始日期</div>
					  <input class="form-control" type="text" id="search-startDate" />
				    </div>
				  </div>
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">结束日期</div>
					  <input class="form-control" type="text" id="search-endDate">
				    </div>
				  </div>
				  
				  <button type="button" id="searchBtn" class="btn btn-default">查询</button>
				  
				</form>
			</div>
			<div class="btn-toolbar" role="toolbar" style="background-color: #F7F7F7; height: 50px; position: relative;top: 5px;">
				<div class="btn-group" style="position: relative; top: 18%;">
					<%--按钮的功能，打开对应的模态窗口--%>
				  <button type="button" class="btn btn-primary" id="addBtn"><span class="glyphicon glyphicon-plus"></span> 创建</button>
				  <button type="button" class="btn btn-default" id="editBtn"><span class="glyphicon glyphicon-pencil"></span> 修改</button>
				  <button type="button" class="btn btn-danger" id="deleteBtn"><span class="glyphicon glyphicon-minus"></span> 删除</button>
				</div>
				
			</div>
			<div style="position: relative;top: 10px;">
				<table class="table table-hover">
					<thead>
						<tr style="color: #B3B3B3;">
							<td><input type="checkbox" id="activityQXBox"/></td>
							<td>名称</td>
                            <td>所有者</td>
							<td>开始日期</td>
							<td>结束日期</td>
						</tr>
					</thead>
					<tbody id="activityBody">
					</tbody>
				</table>
			</div>
			
			<div style="height: 50px; position: relative;top: 30px;">
				<div id="activityPage"></div>
			</div>
			
		</div>
		
	</div>
</body>
</html>
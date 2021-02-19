<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<% String basePath
=request.getScheme()+"://"+request.getServerName()+":"+
request.getServerPort()+request.getContextPath()+"/"; %>

<!DOCTYPE html>
<html>
<head>
	<link rel="shortcut icon" href="#"/>
	<base href="<%=basePath%>">
<meta charset="UTF-8">
<link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet" />
<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>
	<script>
		$(function(){
			if(window.top!=window)//如果登陆页面不是顶级窗口，就将他设为顶级窗口
			{
				window.top.location = window.location;
			}

			$("#loginAct").val(" ");

			$("#loginAct").focus();

			$("#buttonSubmit").click(function () {
				login();
			})

			$(window).keydown(function (event) {
				if(event.keyCode==13)
				{
					login();
				}
			})
		})

		function login() {
			var account = $.trim($("#loginAct").val());
			var pwd = $.trim($("#loginPwd").val());
			if(account=="" || pwd=="")
			{
				$("#msg").html("账号和密码不能为空");
				return false;
			}

			$.ajax({
				url:"settings/user/login.do",//前面不带/
				data:{"loginAct":account,"loginPwd":pwd},//给service层的数据
				type:"post",//有密码，因此不显示
				dataType:"json",//固定不变，都用json
				success: function (data) {//假设data是{"success":true/false,"errorMsg":"哪错了"}
					if(data.success)
					{
						window.location.href = "workbench/index.jsp";//直接跳转
					}
					else{
						$("#msg").html(data.errorMsg);
					}
				}
			})
		}
	</script>
</head>
<body>
	<div style="position: absolute; top: 0px; left: 0px; width: 60%;">
		<img src="image/IMG_7114.JPG" style="width: 100%; height: 90%; position: relative; top: 50px;">
	</div>
	<div id="top" style="height: 50px; background-color: #3C3C3C; width: 100%;">
		<div style="position: absolute; top: 5px; left: 0px; font-size: 30px; font-weight: 400; color: white; font-family: 'times new roman'">CRM &nbsp;<span style="font-size: 12px;">&copy;2021&nbsp;wagchoir</span></div>
	</div>
	
	<div style="position: absolute; top: 120px; right: 100px;width:450px;height:400px;border:1px solid #D5D5D5">
		<div style="position: absolute; top: 0px; right: 60px;">
			<div class="page-header">
				<h1>登录</h1>
			</div>
			<form action="workbench/index.jsp" class="form-horizontal" role="form">
				<div class="form-group form-group-lg">
					<div style="width: 350px;">
						<input class="form-control" type="text" placeholder="用户名" id="loginAct">
					</div>
					<div style="width: 350px; position: relative;top: 20px;">
						<input class="form-control" type="password" placeholder="密码" id="loginPwd">
					</div>
					<div class="checkbox"  style="position: relative;top: 30px; left: 10px;">
						
						<span id="msg" style="color: #ff0000">请输入账户名和密码</span>

					</div>
					<button type="button" id="buttonSubmit" class="btn btn-primary btn-lg btn-block"  style="width: 350px; position: relative;top: 45px;">登录</button>
				</div>
			</form>
		</div>
	</div>
</body>
</html>
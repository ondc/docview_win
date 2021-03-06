<%@ page contentType="text/html; charset=utf-8"%>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <title>HOME - I Doc View</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="在线文档预览、文档协作编辑、幻灯片远程控制、同步信息展示等，支持格式：doc, docx, xls, xlsx, ppt, pptx, pdf和txt等。">
    <meta name="keywords" content="在线 文档 预览 同步 协作 Online Document Preview doc view viewer office word excel" />
    <meta name="copyright" content="I Doc View 2014">
    <meta name="author" content="godwin668@gmail.com">

    <!-- Le styles -->
    <link href="/static/bootstrap/css/bootstrap.css" rel="stylesheet">
    <link href="/static/idocv/css/style.css?v=${version}" rel="stylesheet">
    <link href="/static/bootstrap/css/bootstrap-responsive.min.css" rel="stylesheet">

    <!-- HTML5 shim, for IE6-8 support of HTML5 elements -->
    <!--[if lt IE 9]>
      <script src="/static/bootstrap/js/html5shiv.js"></script>
    <![endif]-->

    <!-- Fav and touch icons -->
    <!-- to be done -->
  </head>

  <body>

    <div class="navbar navbar-inverse navbar-fixed-top">
      <div class="navbar-inner">
        <div class="container-fluid">
          <button type="button" class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
          <a class="brand" href="http://www.idocv.com">I Doc View</a>
          <!-- SIGN UP & SIGN IN HERE -->
        </div>
      </div>
    </div>

    <div class="container-fluid">
      <div class="row-fluid">
        <div class="span12">
       		<!-- PAGE CONTENT HERE -->
       		<form id="form-signup" class="form-horizontal" action='' method="POST" style="max-width: 600px; margin: 0 auto;">
			  <fieldset>
			    <div id="legend">
			      <legend class="">注册</legend>
			    </div>
			    
			    <!-- ERROR RESULT HERE -->
			    <div id="sign-up-result"></div>
			    
			    <div class="control-group">
			      <!-- Username -->
			      <label class="control-label"  for="username">用户名</label>
			      <div class="controls">
			        <input type="text" id="username" name="username" placeholder="至少3个字符" class="input-xlarge" data-validation="validate_min_length length3" >
			        <p class="help-block"></p>
			      </div>
			    </div>
			    
			    <div class="control-group">
			      <!-- E-mail -->
			      <label class="control-label" for="email">E-mail</label>
			      <div class="controls">
			        <input type="text" id="email" name="email" placeholder="example@idocv.com" class="input-xlarge" data-validation="validate_email" />
			        <p class="help-block"></p>
			      </div>
			    </div>
			
			    <div class="control-group">
			      <!-- Password-->
			      <label class="control-label" for="password">密码</label>
			      <div class="controls">
			        <input type="password" id="password" name="password" placeholder="至少4个字符" class="input-xlarge" data-validation="validate_confirmation validate_min_length length4" />
			        <p class="help-block"></p>
			      </div>
			    </div>
			
			    <div class="control-group">
			      <!-- Password -->
			      <label class="control-label"  for="password_confirm">确认密码</label>
			      <div class="controls">
			        <input type="password" id="password_confirmation" name="password_confirmation" placeholder="输入相同的密码" class="input-xlarge">
			        <p class="help-block"></p>
			      </div>
			    </div>

			    <div class="control-group">
			      <!-- Button -->
			      <div class="controls">
			        <button class="btn btn-success">注册</button>
			      </div>
			    </div>
			    <div class="control-group">
			      <!-- Button -->
			      <div class="controls">
					已有账号？ 请直接<a href="/user/login">登录</a>
			      </div>
			    </div>
			  </fieldset>
			</form>
        </div><!--/span-->
      </div><!--/row-->

      <hr>

      <footer>
			Powered by: <a href="http://www.idocv.com">I Doc View</a>&nbsp;&nbsp;&nbsp;Email: <a href="mailto:support@idocv.com">support@idocv.com</a>
      </footer>

    </div><!--/.fluid-container-->

    <!-- Le javascript
    ================================================== -->
    <script src="/static/jquery/js/jquery-1.11.1.min.js?v=${version}"></script>
    <script src="/static/bootstrap/js/bootstrap.js?v=${version}"></script>
	<script src="/static/jquerycookie/js/jquery.cookie.js?v=${version}"></script>
	<script src="/static/urlparser/js/purl.js?v=${version}"></script>
	<script src="/static/formvalidator/js/jquery.formvalidator.min.js?v=${version}"></script>
<!-- 	<script src="/static/idocv/js/user.js?v=${version}"></script> -->
    <script src="/static/idocv/js/user-signup.js?v=${version}"></script>

  </body>
</html>
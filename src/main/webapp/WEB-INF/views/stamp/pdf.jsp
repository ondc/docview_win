﻿<%@ page contentType="text/html; charset=utf-8"%>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <title>PDF - I Doc View</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="在线文档预览、文档协作编辑、幻灯片远程控制、同步信息展示等，支持格式：doc, docx, xls, xlsx, ppt, pptx, pdf和txt等。">
    <meta name="keywords" content="在线 文档 预览 同步 协作 Online Document Preview doc view viewer office word excel" />
    <meta name="copyright" content="I Doc View 2014">
    <meta name="author" content="godwin668@gmail.com">

    <!-- styles -->
    <link href="/static/bootstrap/css/bootstrap.min.css" rel="stylesheet" />
    <link href="/static/idocv/css/style.css" rel="stylesheet" />
    <link href="/static/bootstrap/css/bootstrap-responsive.min.css" rel="stylesheet" />
    <link href="/static/pdf2htmlEX/css/base.css" rel="stylesheet" />
    <link href="/static/pdf2htmlEX/css/fancy.css" rel="stylesheet" />
    <style type="text/css">
    	#container {
    		position: absolute;
    		top: 0px;
    		left: 0px;
    		right: 0px;
    		bottom: 0px;
/*     		border: 3px solid blue; */
    	}
    	.kineticjs-content {
    		position: absolute;
    		top: 0px;
    		left: 0px;
    		right: 0px;
    		bottom: 0px;
/*     		border: 5px solid green; */
    	}
    	.file-select-button {
			position: relative;
			overflow: hidden;
			margin-right: 4px;
			margin-bottom: -10px;
		}
		.file-select-button input {
			position: absolute;
			top: 0;
			right: 0;
			margin: 0;
			opacity: 0;
			filter: alpha(opacity=0);
			transform: translate(-300px, 0) scale(4);
			font-size: 23px;
			direction: ltr;
			cursor: pointer;
		}
    	.footer-fixed {
    		background-color: lightblue;
    		position: fixed;
			bottom: 0px;
			left: 0px;
			right: 0px;
			margin: 0px;
			padding: 5px;
			text-align: center;
    	}
    	.paging-bottom-all {
    		display: none;
    	}
    </style>

    <!--[if lt IE 9]>
      <script src="/static/bootstrap/js/html5shiv.js"></script>
    <![endif]-->
  </head>

  <body class="pdf-body">
    <div class="loading-mask" style="display: block;">
      <div class="loading-zone">
        <div class="text">正在载入...0%</div>
        <div class="progress progress-striped active">
          <div class="bar" style="width: 0%;"></div>
        </div>
      </div>
      <div class="brand">
        <footer>
          Powered by: <a href="http://www.idocv.com">I Doc View</a>&nbsp;&nbsp;&nbsp;Email: <a href="mailto:support@idocv.com">support@idocv.com</a>
        </footer>
      </div>
    </div>

    <div class="navbar navbar-inverse navbar-fixed-top">
      <div class="navbar-inner">
        <div class="container-fluid">
          <button type="button" class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
          <!-- FILE NAME HERE -->
          <!-- SIGN UP & SIGN IN -->
        </div>
      </div>
    </div>

    <div class="container-fluid">
      <div class="row-fluid">
        <div class="span12">
          <!-- PDF PAGES HERE -->
          <!-- <div class="word-page"><div class="word-content">WORD CENTENT HERE</div></div> -->
          <!-- 
          <div class="pdf-page">
            <div class="pdf-content">
              <!-- WORD CENTENT HERE
            </div>
          </div>
           -->
        </div><!--/span-->
      </div><!--/row-->

      <hr>

      <footer>
        Powered by: <a href="http://www.idocv.com">I Doc View</a>&nbsp;&nbsp;&nbsp;Email: <a href="mailto:support@idocv.com">support@idocv.com</a>
      </footer>
      
      <footer class="footer-fixed">
        <div style="margin-bottom: 20px; display: inline;">&nbsp;在第</div>
        <select class="span1 slct-stamp-page" style="margin-bottom: 0px;">
          <option value="no">无</option>
          <option value="1">1</option>
          <option value="2">2</option>
          <option value="3">3</option>
          <option value="4">4</option>
          <option value="5">5</option>
          <option>全部</option>
        </select>
        &nbsp;页加盖
        <span class="button file-select-button">
          <span><button class="btn btn-primary" type="button">&nbsp;&nbsp;图章&nbsp;&nbsp;</button></span>
          <input id="fileupload" type="file" name="file" multiple>
        </span>
        &nbsp;并生成&nbsp;
        <button class="btn btn-primary btn-stamp-pdf-generator" type="button">PDF</button>
      </footer>
      
      <div class="progress progress-striped active bottom-paging-progress">
        <div class="bar" style="width: 0%;"></div>
      </div>
      <div class="paging-bottom-all">
        <!-- SUB PAGING DIV(s) HERE -->
        <!-- 
        <div class="paging-bottom-sub" page-num="1" style="width: 20%;">1</div>
        ...
         -->
      </div>

    </div><!--/.fluid-container-->
    
    <!-- JavaSript
    ================================================== -->
    <script src="/static/jquery/js/jquery-1.11.1.min.js?v=${version}"></script>
    <script src="/static/bootstrap/js/bootstrap.min.js?v=${version}"></script>
    <script src="/static/idocv/js/progress.js?v=${version}"></script>
    <script src="/static/jquerycookie/js/jquery.cookie.js?v=${version}"></script>
    <script src="/static/idocv/js/custom.js?v=${version}"></script>
    <script src="/static/scrollspy/js/jquery-scrollspy.js"></script>
    <script src="/static/urlparser/js/purl.js"></script>
    <script src="/static/formvalidator/js/jquery.formvalidator.min.js"></script>
    <script src="/static/infinite-scroll/js/jquery.infinitescroll.js"></script>
    <script src="/static/kinetic/js/kinetic-v4.7.4.min.js"></script>
    <script src="/static/idocv/js/stamp-pdf.js"></script>
    <script src="/static/idocv/js/stat.js"></script>
  </body>
</html>
<!DOCTYPE HTML>
<html>
<meta http-equiv="content-type" content="text/html;charset=utf-8" />
<head>
    <title>藏書閣 - 電子書籍分享平台</title>
    <meta charset="utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta name="viewport" content="width=device-width, initial-scale=1, user-scalable=no" />
	<meta name="apple-mobile-web-app-title" content="藏書閣" />
    <!--[if lte IE 8]><script src="/assets/js/ie/html5shiv.js"></script><![endif]-->
    <link rel="stylesheet" href="assets/css/main.css" />
    <link rel="icon" href="assets/icons/favicon.ico" type="image/x-icon" />
    <link rel="apple-touch-icon-precomposed" href="assets/icons/apple-touch-icon-precomposed.png" />
    <!--[if lte IE 8]><link rel="stylesheet" href="/assets/css/ie8.css" /><![endif]-->
</head>
<body class="is-faq">
    <header id="header">
        <p><font size="8" color="#666666" face="DFKai-sb">藏書閣</font></p>
        <p>電子書籍分享平台</p>
        <ul class="actions">
        	<li><font size="4"><a href="#" onclick="history.back()" class="button">返回書架</a></font></li><br><br>
        </ul>
    </header>
    <div id="main">
        <article id="modal" class="container">
            <header><h2>資料修改</h2></header>
            <div id="doc" class="content">
            	<form id="form" action="doUserModify?token=$MemberModel.userPWHash" method="POST">
					<p>
						<font size="5" color="#888888">使用者帳號：</font>
						<input type="text" name="userID" size="10" value="$MemberModel.userID" disabled="disabled"/><br>
						<font size="5" color="#888888">密碼：</font>
						<input type="password" name="userPWHash" size="10" requiredaria-required="true" pattern="\w{3,}+"/><br>
						<font size="5" color="#888888">姓名：</font>
						<input type="text" name="userName" size="10" value="$MemberModel.userName" requiredaria-required="true" pattern="\w{1,}+"/><br>
						<font size="5" color="#888888">E-mail：</font>
						<input type="text" name="userEmail" size="10" value="$MemberModel.userEmail" requiredaria-required="true" pattern="^\w+\.*\w+@(\w+\.){1,5}[a-zA-Z]{2,3}$+"/>
						<input type="hidden" name="enable" value="0">
					</p>
					<p align="right">
						<input type="submit" value="送出修改" />
						<input type ="button" onclick="history.back()" value="返回" />
					</p>
				</form>
            </div>
        </article>
    </div>
    <footer></footer>
    <script src="assets/js/jquery.min.js"></script>
    <script src="assets/js/skel.min.js"></script>
    <!--[if lte IE 8]><script src="/assets/js/ie/respond.min.js"></script><![endif]-->
    <script src="assets/js/main.js"></script>
</body>
</html>
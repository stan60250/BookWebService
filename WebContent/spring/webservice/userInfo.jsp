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
            <header><h2>帳號資訊</h2></header>
            <div class="content">
				<p>
					<table>
						<tbody>
							<tr>
								<th scope="row">使用者帳號：</th>
								<td>$InfoModel.userID</td>
							</tr>
							<tr>
								<th scope="row">密碼：</th>
								<td><a href="userModify?token=$InfoModel.userToken">點我修改密碼</a></td>
							</tr>
							<tr>
								<th scope="row">姓名：</th>
								<td>$InfoModel.userName</td>
							</tr>
							<tr>
								<th scope="row">E-mail：</th>
								<td>$InfoModel.userEmail</td>
							</tr>
							<tr>
								<th scope="row">點數：</th>
								<td>$InfoModel.userPoint</td>
							</tr>
						</tbody>
					</table>
				</p>
				<p align="right">
					<input type ="button" onclick="history.back()" value="返回" />
				</p>
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
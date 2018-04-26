<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!doctype html>
<html>
<head>
    <title>Error Page</title>
    <style type="text/css">
        .center {
            text-align: center;
        }
        #expand {
            color: #73a4ff;
            border-bottom: 1px dashed #73a4ff;
            display: inline-block;
            cursor: pointer;
            *display: inline;
            zoom: 1;
        }
    </style>
</head>
<body>
<s:actionerror/>
<div class="center">
    <h2>В процессе работы возникла ошибка</h2>
    Попробуйте обновить страницу, нажав <b>Ctrl + F5</b><br>
    <jsp:useBean id="exception" class="java.lang.Exception" scope="request"/>
	<%  
		//StringBuilder errorMessage = new StringBuilder();
		//errorMessage.append("Exception message: "+exception.getMessage()+"\n");
		out.println("<pre>Exception message: "+exception.getMessage()+"</pre>");
		Throwable innerException = exception.getCause();
        while (innerException!=null) {
        	//errorMessage.append("Caused by: "+innerException.getMessage()+"\n");
        	out.println("<pre>Caused by: "+innerException.getMessage()+"</pre>");
        	innerException = innerException.getCause();
        }
        
        // Send the error using email
        //MailService mailService = SpringBeansProvider.getBean("mailService", MailService.class);
        //mailService.sendErrorMail(errorMessage.toString());
    %>
    <span id="expand">Подробнее</span>
</div>

<div id="more1" style="display:none;">
    <s:set name="msg" value="%{exception.message}"/>
    <h3><%--<s:property value="@ru.tndmsoft.common.core.mail.EmailQueueWorker@formatHtml(#msg)"/>--%></h3>
    <br>
    <pre cols="10" rows="15" style="width:100%">
        <s:property value="%{exceptionStack}"/>
    </pre>

    <%--<s:set name="emailError" value="@ru.tndmsoft.common.core.mail.EmailQueueWorker@emailError(#session,#request,exception.message,exceptionStack,exception.oraSession)"/>
    <s:if test="#emailError != true">
        <s:set name="emailError" value="@ru.tndmsoft.common.core.mail.EmailQueueWorker@emailError(#session,#request,exception.message,exceptionStack)"/>
    </s:if>--%>
</div>
<script type="text/javascript">
    (function(){
        var more1 = document.getElementById('more1'),
                expand = document.getElementById('expand');
        expand.onclick = function(){
            if (more1.style.display === '') {
                more1.style.display = 'none';
            } else {
                more1.style.display = ''
            }
        };
    })();
</script>
</body>
</html>

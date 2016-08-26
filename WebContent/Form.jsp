<%@ page import="pojo.Region, pojo.Manager, java.util.HashMap, java.util.Map.Entry, java.util.List" %>
<%
String lineBreaker="&#013;&#010;";
String managerNick = request.getParameter("manager");
String region = request.getParameter("region");
Region selectedRegion = null;
List<Manager> managerList = null;
Manager selectedManager = null;
String typeAttrib = request.getParameter("type");
%>

<html>
    <head>
    	<%@ page pageEncoding="utf-8"%>
		<meta charset="utf-8">
		<link href="./css/style.css" rel="stylesheet">
		<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.2.6/jquery.min.js"></script>
		<link rel="icon" href="favicon.ico" type="image/x-icon">
		<link rel="shortcut icon" href="favicon.ico" type="image/x-icon">

		<title>OfferMaker</title>
		
		
		 <script>
		$(document).ready(function(){
		
		$( "#add" ).click(function() {
			
			$( "#add_container" ).append('<div class="header"><span>Добавление модуля:</span></div><div class="content"><input class="input" size="85" type="text" name="modulename" value="" placeholder="Разработка и установка модуля «названиемодуля»"/><input class="input" size="85" type="text" name="moduleprice" value="" placeholder="X XXX"/></div>');
			
		});
		});
		</script>
		
    </head>
    <body>
	<div id="wrapper">
		
		<% HashMap<String, Region> regionList = (HashMap<String, Region>)request.getAttribute("regionList");%>
		<form name="regionform" class="region-form" method="get" enctype="multipart/form-data">
			<div class="header">
				<h1>OfferGenerator</h1>
				<h2>Выберите бланк:</h2>
				<span>В зависимости от выбранного бланка подгружаются контактные данные менеджера</span>
			</div>
			<div class="content">
				<select class="input" name="type" required>
					<option disabled selected="selected">Тип коммерческого предложения</option>
					<option value="promo" <%=(typeAttrib != null && typeAttrib.equals("promo") ? "selected=\"selected\"" : "") %>>По продвижению сайта</option>
					<option value="creation" <%=(typeAttrib != null && typeAttrib.equals("creation") ? "selected=\"selected\"" : "") %> >По созданию сайта</option>
				</select>
				<%
				if(typeAttrib != null && (typeAttrib.equals("promo") || typeAttrib.equals("creation"))){
					out.println("<select class=\"input\" name=\"region\" required>");
					out.println("<option disabled selected=\"selected\"'>Выберите регион</option>");
						if(regionList != null){
							String temp = "";
							for(Entry<String, Region> reg : regionList.entrySet()){
								temp = reg.getKey();
								out.println("<option value=\"" + temp + "\"" + (temp.equals(region) ? " selected=\"selected\"" : "") + ">" + reg.getValue().getName() + "</option>");
							}
						}
					out.println("</select>");
					
					selectedRegion = regionList.get(region);
					if(selectedRegion != null){
						managerList = selectedRegion.getManagers();
					}
					
					if(region != null & regionList != null){
						if(selectedRegion != null & managerList != null){
							out.println("<select class=\"input\" name=\"manager\">");
							out.println("<option disabled selected=\"selected\">Выберите имя менеджера</option>");
							for(Manager mng : managerList){
								String temp = mng.getNick();
								out.println("<option value=\"" + temp + "\"" + (temp.equals(managerNick) ? " selected=\"selected\"" : "") + ">" + mng.getName() + "</option>");
								if(temp.equals(managerNick))
									selectedManager=mng;
							}
							out.println("</select>");
						}	
					}
				}
				%>
			</div>
			<div class="footer">
				<input type="submit" class="button" value="Подтвердить" />
			</div>	
		</form>
		
	<%	if(typeAttrib != null)
			if(typeAttrib.equals("promo")){%>
			
		<form name="commform" method="post" class="region-form" action="promoupload" enctype="multipart/form-data">
            <div class="top"><div class="header"><h2> CSV-файл с фразами:</h2></div>
			<div class="content">	
				<input type="file" accept="text/csv" class="upload" name="uploadFile" required/>
			</div>
			</div>
				<div class="header"><h2>Контакты Delta в данном регионе:</h2></div>
				<%
					if(selectedRegion == null){%>
						<div class="header"><span>Адрес:</span></div>
						<div class="content"><input class="input" size="85" type="text" name="address" value="" /></div>
						<div class="header"><span>Телефоны:</span></div>
						<div class="content"><input class="input" size="85" type="text" name="phonenumbers" value=""/></div>
						<div class="header"><span>Сайт:</span></div>
						<div class="content"><input class="input" size="85" type="text" name="deltasite" value=""/></div>
				<%	}else{%>
						<div class="header"><span>Адрес:</span></div>
						<div class="content"><input class="input" size="85" type="text" name="address" value="<%=selectedRegion.getAddress() %>" /></div>
						<div class="header"><span>Телефоны:</span></div>
						<div class="content"><input class="input" size="85" type="text" name="phonenumbers" value="<%=selectedRegion.getPhones() %>"/></div>
						<div class="header"><span>Сайт:</span></div>
						<div class="content"><input class="input" size="85" type="text" name="deltasite" value="<%=selectedRegion.getSite() %>"/></div>
				<%}%>
				
				<div class="header"><h2>Контакты МЕНЕДЖЕРА в данном регионе:</h2></div>
				
				<%
					if(selectedManager == null){%>
						<div class="header"><span>Имя</span></div>
						<div class="content"><input class="input" size="85" type="text" name="managername" value=""/></div>
						<div class="header"><span>Должность</span></div>
						<div class="content"><input class="input" size="85" type="text" name="managerposition" value=""/></div>
						<div class="header"><span>Почта</span></div>
						<div class="content"><input class="input" size="85" type="text" name="email" value=""/></div>
						<div class="header"><span>Телефон</span></div>
						<div class="content"><input class="input" size="85" type="text" name="phonenumber" value=""/></div>
				<%	}else{ %>
						<input type="hidden" name = "managernick" value=<%=selectedManager.getNick() %>>
						<div class="mngpic"><img alt="Photo" src="/OfferMaker/config/regions/pic/<%=selectedManager.getNick() + ".jpeg" %>"></div>
						<div class="header headermng"><span>Имя</span></div><br>
						<div class="content contentmng"><input class="input" size="50" type="text" name="managername" value="<%=selectedManager.getName() %>"/></div><br>
						<div class="header headermng"><span>Должность</span></div><br>
						<div class="content contentmng"><input class="input" size="50" type="text" name="managerposition" value="<%=selectedManager.getPosition() %>"/></div><br>
						<div class="header headermng"><span>Почта</span></div><br>
						<div class="content contentmng"><input class="input" size="50" type="text" name="email" value="<%=selectedManager.getEmail() %>"/></div><br>
						<div class="header headermng"><span>Телефон</span></div><br>
						<div class="content contentmng"><input class="input" size="50" type="text" name="phonenumber" value="<%=selectedManager.getPhonenumber() %>"/></div>
				<%}%>
				<br><br>
				<div class="header"><span>URL сайта клиента (<b>ВСЕГДА ПИСАТЬ С WWW!</b>)</span></div>
				<div class="content"><input class="input" size="85" type="text" name="sitename" value="www."/></div>
				<div class="header"><span>Обращение</span></div>
				<div class="content"><input class="input" size="85" type="text" name="appeal" value="Уважаемые Господа"/></div>
				<div class="header"><span>Текст с обращением</span></div>
				<div class="content"><textarea class="input" rows="8" cols="85" name="audittext">Мы провели аудит вашего рынка и конкурентной среды, в связи с чем предлагаем комплексное продвижение сайта в поисковых системах Яндекс, Google, на первую страницу (ТОР-10) по более чем 70% ключевых фраз и поддержку результатов продвижения.</textarea></div>
				<div class="header"><span>Регионы продвижения (каждый с красной строки)</span></div>
				<div class="content"><textarea class="input" rows="8" cols="85" name="regiontext"><%=(selectedRegion != null ? selectedRegion.getName() + lineBreaker : "")%>Продвижение сайта предполагается осуществлять по предложенным запросам с добавлением городов !!!ЗАМЕНИТЬ!!! и их производных, например: «ПРИМЕР 1», «ПРИМЕР 2»</textarea></div>
				<div class="header"><h2>Данные о сроках и ценах:</h2></div>
				<div class="header"><span>Стоимость, руб./мес.</span></div>
				<div class="content"><input class="input" size="85" type="text" name="price" value="10 000"/></div>
				<div class="header"><span>Первоначальные результаты</span></div>
				<div class="content"><input class="input" size="85" type="text" name="firstresult" value="до 3 месяцев"/></div>
				<div class="header"><span>Максимальные результаты</span></div>
				<div class="content"><input class="input" size="85" type="text" name="maxresult" value="от 4 до 6 месяцев"/></div>
				<div class="header"><span>Технические тексты, стоимость, руб. (единоразово)</span></div>
				<div class="content"><input class="input" size="85" type="text" name="techtext" value="4 000"/></div>
				<div class="header"><span>Коммерческие тексты, стоимость, руб. (единоразово)</span></div>
				<div class="content"><input class="input" size="85" type="text" name="commtext" value="6 000"/></div>
				<div class="header"><span>Сроки</span></div>
				<div class="content"><input class="input" size="85" type="text" name="texttime" value="1 месяц"/></div>
				<div class="header"><span>Модификация и адаптация под сайт модуля размещения оптимиз. контента</span></div>
				<div class="content"><input class="input" size="85" type="text" name="sitemod" value="ЗА НАШ СЧЕТ"/></div>
				
				
				<div class="footer">
					<input class="button" type="submit" value="Делать" />
				</div>
   		 	</form>
	<%	}else if(typeAttrib.equals("creation")){%>
			<form name="commform" method="post" class="region-form" action="creationupload" accept-charset="utf-8" enctype="multipart/form-data">
			<div class="header"><h2>Контакты Delta в данном регионе:</h2></div>
			<%
				if(selectedRegion == null){%>
					<div class="header"><span>Адрес:</span></div>
					<div class="content"><input class="input" size="85" type="text" name="deltaaddress" value="" /></div>
					<div class="header"><span>Телефоны:</span></div>
					<div class="content"><input class="input" size="85" type="text" name="deltaphonenumbers" value=""/></div>
					<div class="header"><span>Сайт:</span></div>
					<div class="content"><input class="input" size="85" type="text" name="deltasite" value=""/></div>
			<%	}else{%>
					<div class="header"><span>Адрес:</span></div>
					<div class="content"><input class="input" size="85" type="text" name="deltaaddress" value="<%=selectedRegion.getAddress() %>" /></div>
					<div class="header"><span>Телефоны:</span></div>
					<div class="content"><input class="input" size="85" type="text" name="deltaphonenumbers" value="<%=selectedRegion.getPhones() %>"/></div>
					<div class="header"><span>Сайт:</span></div>
					<div class="content"><input class="input" size="85" type="text" name="deltasite" value="<%=selectedRegion.getSite() %>"/></div>
			<%}%>
			
			<div class="header"><h2>Контакты МЕНЕДЖЕРА в данном регионе:</h2></div>
			
			<%
				if(selectedManager == null){%>
					<div class="header"><span>Имя</span></div>
					<div class="content"><input class="input" size="85" type="text" name="managername" value=""/></div>
					<div class="header"><span>Должность</span></div>
					<div class="content"><input class="input" size="85" type="text" name="managerposition" value=""/></div>
					<div class="header"><span>Почта</span></div>
					<div class="content"><input class="input" size="85" type="text" name="manageremail" value=""/></div>
					<div class="header"><span>Телефон</span></div>
					<div class="content"><input class="input" size="85" type="text" name="managerphone" value=""/></div>
			<%	}else{ %>
					<input type="hidden" name ="managernick" value=<%=selectedManager.getNick() %>>
					<div class="mngpic"><img alt="Photo" src="/OfferMaker/config/regions/pic/<%=selectedManager.getNick() + ".jpeg" %>"></div>
					<div class="header headermng"><span>Имя</span></div><br>
					<div class="content contentmng"><input class="input" size="50" type="text" name="managername" value="<%=selectedManager.getName() %>"/></div><br>
					<div class="header headermng"><span>Должность</span></div><br>
					<div class="content contentmng"><input class="input" size="50" type="text" name="managerposition" value="<%=selectedManager.getPosition() %>"/></div><br>
					<div class="header headermng"><span>Почта</span></div><br>
					<div class="content contentmng"><input class="input" size="50" type="text" name="manageremail" value="<%=selectedManager.getEmail() %>"/></div><br>
					<div class="header headermng"><span>Телефон</span></div><br>
					<div class="content contentmng"><input class="input" size="50" type="text" name="managerphone" value="<%=selectedManager.getPhonenumber() %>"/></div>
			<%}%>
			
			<div class="header"><h2>Название компании клиента</h2></div>
			<div class="content"><input class="input" size="85" type="text" name="companyname" value=""/></div>
			
			<div class="header"><h1>Расценки</h1></div>
			
			<div class="header"><h3>Разработка дизайна</h3></div>
			<div class="header"><span>Обсуждение и согласование деталей проекта</span></div>
			<div class="content"><input class="input" size="85" type="text" name="discuss" value=""/></div>
			<div class="header"><span>Прототипирование</span></div>
			<div class="content"><input class="input" size="85" type="text" name="prototype" value="1 500"/></div>
			<div class="header"><span>Разработка и согласование дизайн-макета</span></div>
			<div class="content"><input class="input" size="85" type="text" name="design" value="7 000"/></div>
			
			<div class="header"><h3>Подготовка шаблонов страниц</h3></div>
			<div class="header"><span>Сбор графических и стилистических ресурсов для шаблонов страниц</span></div>
			<div class="content"><input class="input" size="85" type="text" name="collect" value="1 000"/></div>
			<div class="header"><span>Верстка шаблонов страниц</span></div>
			<div class="content"><input class="input" size="85" type="text" name="template" value="3 500"/></div>
			<div class="header"><span>Настройка шаблонов под систему управления контентом MODx Evolution</span></div>
			<div class="content"><input class="input" size="85" type="text" name="templateconf" value="1 200"/></div>
			
			<div class="header"><h3>Хостинг и система управления контентом MODx Evolution</h3></div>
			<div class="header"><span>Настройка хостинга для нового сайта</span></div>
			<div class="content"><input class="input" size="85" type="text" name="hosting" value="700"/></div>
			<div class="header"><span>Установка и настройка системы управления контентом MODx Evolution</span></div>
			<div class="content"><input class="input" size="85" type="text" name="cms" value="2 500"/></div>
			<div class="header"><span>Установка подготовленных шаблонов на MODx Evolution</span></div>
			<div class="content"><input class="input" size="85" type="text" name="templateinst" value="500"/></div>
			
			<div class="header"><h3>Разработка модулей сайта</h3></div>
			
			<div id="add_container"></div><div id="add">Добавить модуль</div>
			
			<div class="header"><h3>Завершающий этап</h3></div>
			<div class="header"><span>Наполнение базовой информацией основных разделов сайта</span></div>
			<div class="content"><input class="input" size="85" type="text" name="filling" value="300"/></div>
			<div class="header"><span>Тестирование ресурса</span></div>
			<div class="content"><input class="input" size="85" type="text" name="test" value="1 500"/></div>
			<div class="header"><span>Ввод проекта в эксплуатацию</span></div>
			<div class="content"><input class="input" size="85" type="text" name="enter" value="800"/></div>
			
			<div class="footer">
				<input class="button" type="submit" value="Делать" />
			</div>
		</form>
	<%	}%>
	</div>
</body>
</html>
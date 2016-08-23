<%@ page import="pojo.Region, pojo.Manager, java.util.HashMap, java.util.Map.Entry, java.util.List" %>
<%
String lineBreaker="&#013;&#010;";
String managerNick = request.getParameter("manager");
String region = request.getParameter("region");
Region selectedRegion = null;
List<Manager> managerList = null;
Manager selectedManager = null;
%>

<html>
    <head>
    	<%@ page pageEncoding="utf-8"%>
		<meta charset="utf-8">
		<meta http-equiv="Cache-Control" content="no-cache">
		<link href="./css/style.css" rel="stylesheet">
		<link rel="icon" href="favicon.ico" type="image/x-icon">
		<link rel="shortcut icon" href="favicon.ico" type="image/x-icon">

		<title>OfferMaker ManagerEdit</title>

    </head>
    <body>
	<div id="wrapper">
		
		<% HashMap<String, Region> regionList = (HashMap<String, Region>)request.getAttribute("regionList");%>
		<form name="regionform" class="region-form" method="get" enctype="multipart/form-data">
			<div class="header">
				<h1>Редактирование данных</h1>
				<h2>Выберите бланк:</h2>
				<span>В зависимости от выбранного бланка подгружаются контактные данные менеджера</span>
			</div>
			<div class="content">
				<%
				out.println("<select class=\"input\" name=\"region\">");
				out.println("<option value=\"\" selected=\"selected\"'>Добавить регион</option>");
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
						out.println("<option value=\"\" selected=\"selected\">Добавление менеджера</option>");
						for(Manager mng : managerList){
							String temp = mng.getNick();
							out.println("<option value=\"" + temp + "\"" + (temp.equals(managerNick) ? " selected=\"selected\"" : "") + ">" + mng.getName() + "</option>");
							if(temp.equals(managerNick))
								selectedManager=mng;
						}
						out.println("</select>");
					}	
				}
				%>
			</div>
			<div class="footer">
				<input type="submit" class="button" value="Выбрать" />
			</div>	
		</form>
			
		<form name="commform" method="post" class="region-form" enctype="multipart/form-data">
				<div class="header"><h2>Контакты Delta в данном регионе:</h2></div>
				<%
					if(selectedRegion == null){%>
						<div class="header"><span>Сокр. название региона (на латинице):</span></div>
						<div class="content"><input class="input" size="85" type="text" name="regionnick" value="" ></div>
						<div class="header"><span>Название региона:</span></div>
						<div class="content"><input class="input" size="85" type="text" name="regionname" value="" /></div>
						<div class="header"><span>Адрес:</span></div>
						<div class="content"><input class="input" size="85" type="text" name="address" value="" /></div>
						<div class="header"><span>Телефоны:</span></div>
						<div class="content"><input class="input" size="85" type="text" name="phonenumbers" value=""/></div>
						<div class="header"><span>Сайт:</span></div>
						<div class="content"><input class="input" size="85" type="text" name="deltasite" value=""/></div>
				<%	}else{%>
						<div class="checkbox"><input type="checkbox" name="deleteRegion">Удалить регион</div>
						<div class="header"><span>Сокр. название региона (на латинице):</span></div>
						<div class="content"><input class="input-disabled" size="85" type="text" name="regionnick" value="<%=selectedRegion.getNick() %>" ></div>
						<div class="header"><span>Название региона:</span></div>
						<div class="content"><input class="input" size="85" type="text" name="regionname" value="<%=selectedRegion.getName() %>" /></div>
						<div class="header"><span>Адрес:</span></div>
						<div class="content"><input class="input" size="85" type="text" name="address" value="<%=selectedRegion.getAddress() %>" /></div>
						<div class="header"><span>Телефоны:</span></div>
						<div class="content"><input class="input" size="85" type="text" name="phonenumbers" value="<%=selectedRegion.getPhones() %>"/></div>
						<div class="header"><span>Сайт:</span></div>
						<div class="content"><input class="input" size="85" type="text" name="deltasite" value="<%=selectedRegion.getSite() %>"/></div>
				<%}%>
				
				<div class="header"><h2>Контакты МЕНЕДЖЕРА в данном регионе:</h2></div>
				<div class="top"><div class="header"><h2>Фото:</h2></div>
					<div class="content">	
							<input type="file" accept="	image/jpeg" class="upload" name="managerPic"/>
					</div>
				</div>
				<%
					if(selectedManager == null){%>
						<div class="header"><span>Ник</span></div>
						<div class="content"><input class="input" size="85" type="text" name="managernick" value="" ></div>
						<div class="header"><span>Имя</span></div>
						<div class="content"><input class="input" size="85" type="text" name="managername" value=""/></div>
						<div class="header"><span>Должность</span></div>
						<div class="content"><input class="input" size="85" type="text" name="managerposition" value=""/></div>
						<div class="header"><span>Почта</span></div>
						<div class="content"><input class="input" size="85" type="text" name="email" value=""/></div>
						<div class="header"><span>Телефон</span></div>
						<div class="content"><input class="input" size="85" type="text" name="phonenumber" value=""/></div>
				<%	}else{ %>
						<div class="checkbox"><input type="checkbox" name="deleteManager">Удалить менеджера</div>
						<div class="mngpic"><img alt="Photo" src="/OfferMaker/config/regions/pic/<%=selectedManager.getNick() + ".jpeg" %>"></div>
						<div class="header headermng"><span>Ник</span></div><br>
						<div class="content contentmng"><input class="input-disabled" size="50" type="text" name="managernick" value="<%=selectedManager.getNick() %>"></div><br>
						<div class="header headermng"><span>Имя</span></div><br>
						<div class="content contentmng"><input class="input" size="50" type="text" name="managername" value="<%=selectedManager.getName() %>"/></div><br>
						<div class="header headermng"><span>Должность</span></div><br>
						<div class="content contentmng"><input class="input" size="50" type="text" name="managerposition" value="<%=selectedManager.getPosition() %>"/></div><br>
						<div class="header headermng"><span>Почта</span></div><br>
						<div class="content contentmng"><input class="input" size="50" type="text" name="email" value="<%=selectedManager.getEmail() %>"/></div><br>
						<div class="header headermng"><span>Телефон</span></div><br>
						<div class="content contentmng"><input class="input" size="50" type="text" name="phonenumber" value="<%=selectedManager.getPhonenumber() %>"/></div>
				<% }%>
				<div class="footer">
					<input class="button" type="submit" value="Добавить/Обновить" />
				</div>
   		 	</form>
	</div>
</body>
</html>
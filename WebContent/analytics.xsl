<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:variable name="webformBaseUrl" select="concat(/data/environment/serverName, '/webform/public/')" />
	<xsl:variable name="webformById" select="concat($webformBaseUrl, /data/form/id)" />
	<xsl:variable name="webformPrettyUrl" select="concat($webformBaseUrl, /data/form/prettyUrl)" />
	<xsl:variable name="webformUrlToUse">
		<xsl:value-of select="$webformBaseUrl" />
		<xsl:choose>
			<xsl:when test="string-length(/data/form/prettyUrl) &gt; 0"><xsl:value-of select="/data/form/prettyUrl" /></xsl:when>
			<xsl:otherwise><xsl:value-of select="/data/form/id" /></xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	<xsl:template match="/">
		<form action="" method="post" name="portal_form">
			<input type="hidden" name="ACTION" />
			<input type="hidden" name="SCREEN" value="ANALYTICS" />
			<input type="hidden" name="FORM_ID" value="{/data/form/id}" />
			<!-- survey content -->
			<xsl:variable name="webformBaseUrl" select="concat(/data/environment/serverName, 'webforms/public/')" />
			<xsl:variable name="webformUrl" select="concat($webformBaseUrl, /data/form/prettyUrl)" />
			<div class="row">
				<div class="col-lg-12">
					<nav>
						<ul class="nav nav-pills">
							<li role="presentation"><a href="javascript:switchTab('GENERAL');">General</a></li>
							<li role="presentation"><a href="javascript:switchTab('QUESTION_LIST');">Questions</a></li>
							<li role="presentation"><a href="javascript:switchTab('REPORTS');">Reports</a></li>
							<li role="presentation" class="active"><a href="#">Analytics</a></li>
						</ul>
					</nav>
					<h2>Analytics Information</h2>
					<div class="row form-inline">
						<div class="col-lg-12">
							<div class="form-group first-column">
								<label for="START_DATE">Start Date</label>
								<div class="input-group">
									<input type="text" class="form-control datepicker" name="START_DATE" id="START_DATE" placeholder="YYYY-MM-DD" readonly="readonly" />
									<span class="input-group-addon" onclick="this.previousSibling.focus();"><span class="fa fa-calendar"><span class="sr-only">start date picker</span></span></span>
								</div>	
							</div>
							<div class="form-group nth-column">
								<label for="END_DATE">End Date</label>
								<div class="input-group">
									<input type="text" class="form-control datepicker" name="END_DATE" id="END_DATE" placeholder="YYYY-MM-DD" readonly="readonly" />
									<span class="input-group-addon" onclick="this.previousSibling.focus();"><span class="fa fa-calendar"><span class="sr-only">start date picker</span></span></span>
								</div>								
							</div>
							<div class="form-group nth-column">
								<a class="btn btn-default" href="javascript:viewAnalytics('html', 'summary');"><span class="fa fa-search" /> HTML Summary</a>
								<a class="btn btn-default" href="javascript:viewAnalytics('pdf', 'summary');"><span class="fa fa-search" /> PDF Summary</a>
								<script>
								function viewAnalytics(paramView, paramType)
								{
									var viewUrl = '<xsl:value-of select="/data/environment/serverName" />/toolbox/survey/' + paramView + '/' + paramType + '/<xsl:value-of select="/data/form/id" />';
									var startDate = document.getElementById('START_DATE').value;
									var endDate = document.getElementById('END_DATE').value;
									viewUrl = viewUrl + '?START_DATE=' + startDate + '<xsl:text disable-output-escaping="yes">&amp;</xsl:text>END_DATE=' + endDate;
									window.open(viewUrl, '_blank');
								}
								
								function defaultDate()
								{
									var startDate = document.getElementById('START_DATE');
									var endDate = document.getElementById('END_DATE');
									
									var today = new Date();
									var todayDD = today.getDate();
									var todayMM = today.getMonth() + 1;									
									var todayYYYY = today.getFullYear();
									
									if(todayDD <xsl:text disable-output-escaping="yes">&lt;</xsl:text> 10)
									{
										todayDD = '0' + todayDD;
									}
									if(todayMM <xsl:text disable-output-escaping="yes">&lt;</xsl:text> 10)
									{
										todayMM = '0' + todayMM;
									}
									
									var todayStr = todayYYYY + '-' + todayMM + '-' + todayDD;
									startDate.value = todayStr;
									endDate.value = todayStr;
								}
								window.onload = defaultDate;
								</script>
							</div>
						</div>
					</div>
					<hr />
					<div class="btn-toolbar">
						<a class="btn btn-default" href="javascript:submitForm();">Save</a>
						<a class="btn btn-default" href="javascript:formListScreen();">Back to Forms</a>
						<a class="btn btn-default" href="{$webformUrlToUse}" target="_blank">View Form</a>
					</div>
				</div>
			</div>
		</form>
	</xsl:template>
</xsl:stylesheet>
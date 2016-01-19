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
			<input type="hidden" name="SCREEN" value="GENERAL" />
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
							<li role="presentation" class="active"><a href="#">Reports</a></li>
							<li role="presentation"><a href="javascript:switchTab('ANALYTICS');">Analytics</a></li>
						</ul>
					</nav>
					<h2>Generate Reports</h2>
					<div class="form-group">
						<label for="PDF_GENERATE">Create PDF</label>
						<p><a class="btn btn-primary" href="survey/pdf/summary/{/data/form/id}" target="_blank">Summary</a></p>
					</div>
					<div class="form-group">
						<label for="CSV_GENERATE">Create Excel CSV</label>
						<p><a class="btn btn-primary" href="survey/csv/summary/{/data/form/id}" target="_blank">Summary</a></p>
					</div>
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
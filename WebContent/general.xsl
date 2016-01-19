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
			<div class="row">
				<div class="col-lg-12 bordered-area">				
					<nav>
						<ul class="nav nav-pills">
							<li role="presentation" class="active"><a href="#">General</a></li>
							<li role="presentation"><a href="javascript:switchTab('QUESTION_LIST');">Questions</a></li>
							<li role="presentation"><a href="javascript:switchTab('REPORTS');">Reports</a></li>
							<li role="presentation"><a href="javascript:switchTab('ANALYTICS');">Analytics</a></li>
						</ul>
					</nav>
					<h2>General Information</h2>
					<div class="form-group">
						<label for="FORM_TITLE">Title</label>
						<input type="text" class="form-control" name="FORM_TITLE" id="FORM_TITLE" value="{/data/form/title}" />
					</div>			
					<div class="form-group">
						<label for="FORM_URL">Standard URL</label>
						<p class="help-block">If you do not care about SEO, feel free to link to this URL.</p>
						<div class="input-group">
							<span class="input-group-addon"><xsl:value-of select="$webformBaseUrl" /></span>
							<input type="text" class="form-control" name="FORM_URL" id="FORM_URL" value="{/data/form/id}" />
							<input type="hidden" name="HIDDEN_FORM_URL" id="HIDDEN_FORM_URL" value="{$webformById}" />
							<a href="{concat($webformBaseUrl, /data/form/id)}" class="input-group-addon" target="_blank"><span class="fa fa-external-link" /></a>
						</div>
					</div>			
					<div class="form-group">
						<label for="FORM_PRETTY_URL">Pretty URL</label>
						<p class="help-block">We recommend a <em>Pretty URL</em>. You may use alphanumeric characters, hyphens, underscores and periods. This will be better for SEO.</p>
						<div class="input-group">
							<span class="input-group-addon"><xsl:value-of select="$webformBaseUrl" /></span>
							<input type="text" class="form-control" name="FORM_PRETTY_URL" id="FORM_PRETTY_URL" value="{/data/form/prettyUrl}" />
							<a href="{$webformPrettyUrl}" class="input-group-addon" target="_blank"><span class="fa fa-external-link" /></a>
						</div>
					</div>		
					<div class="form-group">
						<label for="FORM_SKIN_URL">Skin URL</label>
						<p class="help-block">If you wish to have a skin around the survey, enter a URL.</p>
						<input type="text" class="form-control" name="FORM_SKIN_URL" id="FORM_SKIN_URL" value="{/data/form/skinUrl}" />
					</div>		
					<div class="form-group">
						<label for="FORM_SKIN_SELECTOR">Skin CSS Selector</label>
						<p class="help-block">If you entered a skin URL, state the CSS selector which is to be replaced.</p>
						<input type="text" class="form-control" name="FORM_SKIN_SELECTOR" id="FORM_SKIN_SELECTOR" value="{/data/form/skinSelector}" />
					</div>
					<div class="btn-toolbar">
						<a class="btn btn-default" href="javascript:document.portal_form.ACTION.value='SAVE_FORM';document.portal_form.submit();">Save</a>
						<a class="btn btn-default" href="javascript:formListScreen();">Back to Forms</a>
						<a class="btn btn-default" href="{$webformUrlToUse}" target="_blank">View Form</a>
					</div>
				</div>
			</div>
		</form>
	</xsl:template>
</xsl:stylesheet>
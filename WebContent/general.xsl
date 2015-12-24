<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
		<form action="" method="post" name="portal_form">
			<input type="hidden" name="ACTION" />
			<input type="hidden" name="SCREEN" value="GENERAL" />
			<input type="hidden" name="FORM_ID" value="{/data/form/id}" />
			<!-- survey content -->
			<xsl:variable name="webformBaseUrl" select="concat(/data/environment/serverName, 'webforms/public/')" />
			<xsl:variable name="webformUrl" select="concat($webformBaseUrl, /data/form/prettyUrl)" />
			<div class="row">
				<div class="col-lg-8">
					<div class="form-group">
						<label for="FORM_TITLE">Title</label>
						<input type="text" class="form-control" name="FORM_TITLE" id="FORM_TITLE" value="{/data/form/title}" />
					</div>			
					<div class="form-group">
						<label for="FORM_PRETTY_URL">Pretty URL</label>
						<p class="help-block">Create a <em>Pretty URL</em> you can use instead of the generic ID-based option.</p>
						<div class="input-group">
							<span class="input-group-addon"><xsl:value-of select="$webformBaseUrl" /></span>
							<input type="text" class="form-control" name="FORM_PRETTY_URL" id="FORM_PRETTY_URL" value="{/data/form/prettyUrl}" />
							<a href="{$webformUrl}" class="input-group-addon" target="_blank"><span class="fa fa-external-link" /></a>
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
						<a class="btn btn-primary" href="javascript:saveForm();">Save</a>
						<a class="btn btn-danger" href="javascript:formListScreen();">Cancel</a>
					</div>
				</div>
				<!-- survey sidebar -->
				<div class="col-lg-4">
					<ul class="nav nav-pills nav-stacked">
						<li role="presentation" class="active"><a href="#">General</a></li>
						<li role="presentation"><a href="javascript:switchTab('QUESTION_LIST');">Questions</a></li>
					</ul>
				</div>
			</div>
		</form>
	</xsl:template>
</xsl:stylesheet>
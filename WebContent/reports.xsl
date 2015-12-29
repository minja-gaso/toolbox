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
					<h2>Generate Reports</h2>
					<div class="form-group">
						<label for="PDF_GENERATE">Generate PDF</label>
						<p><a class="btn btn-default" href="pdf/survey/{/data/form/id}" target="_blank">View PDF</a></p>
						<p><a class="btn btn-default" href="html/survey/{/data/form/id}" target="_blank">View HTML</a></p>
					</div>
					<div class="btn-toolbar">
						<a class="btn btn-primary" href="javascript:document.portal_form.ACTION.value='SAVE_FORM';document.portal_form.submit();">Save</a>
						<a class="btn btn-danger" href="javascript:formListScreen();">Cancel</a>
					</div>
				</div>
				<!-- survey sidebar -->
				<div class="col-lg-4">
					<ul class="nav nav-pills nav-stacked">
						<li role="presentation"><a href="javascript:switchTab('GENERAL');">General</a></li>
						<li role="presentation"><a href="javascript:switchTab('QUESTION_LIST');">Questions</a></li>
						<li role="presentation" class="active"><a href="#">Reports</a></li>
					</ul>
				</div>
			</div>
		</form>
	</xsl:template>
</xsl:stylesheet>
<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
		<form action="" method="post" name="portal_form">
			<input type="hidden" name="ACTION" />
			<input type="hidden" name="SCREEN" value="CREATE" />
			<div class="form-group">
				<div class="radio">
					<label>
						<input type="radio" name="FORM_TYPE" value="self_assessment" /> <strong>Self-Assessment</strong>
					</label>
				</div>
				<span>The self-assessment survey is used to retrieve results to user taking survey.</span>
			</div>
			<div class="form-group">
				<div class="radio">
					<label>
						<input type="radio" name="FORM_TYPE" value="survey" /> <strong>Survey</strong>
					</label>
				</div>
				<span>The standard survey you are accustomed to.</span>
			</div>
			<div class="btn-toolbar">
				<a class="btn btn-primary" href="javascript:createFormSubmit();">Next</a>
				<a class="btn btn-danger">Cancel</a>
			</div>
		</form>
	</xsl:template>
</xsl:stylesheet>
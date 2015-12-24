<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
		<form action="" method="post" name="portal_form">
			<input type="hidden" name="ACTION" />
			<input type="hidden" name="SCREEN" value="QUESTION_TYPE_TEXT" />
			<input type="hidden" name="QUESTION_TYPE" value="text" />
			<input type="hidden" name="FORM_ID" value="{/data/form/id}" />
			<input type="hidden" name="QUESTION_ID" value="{/data/form/question/id}" />
			<!-- survey content -->
			<div class="row">
			<div class="col-lg-8">
				<nav>
					<ul class="nav nav-pills">
						<li role="presentation" class="active"><a href="#">Text</a></li>
						<li role="presentation"><a href="javascript:switchTab('QUESTION_TYPE_TEXTAREA');">Textarea</a></li>
						<li role="presentation"><a href="javascript:switchTab('QUESTION_TYPE_RADIO');">Radio</a></li>
						<li role="presentation"><a href="javascript:switchTab('QUESTION_TYPE_CHECKBOX');">Checkbox</a></li>
					</ul>
				</nav>
				<hr />
				<xsl:for-each select="/data/form/question">
					<div class="form-group">
						<label for="QUESTION_LABEL">Label</label>
						<input type="text" class="form-control" name="QUESTION_LABEL" id="QUESTION_LABEL" value="{label}" />
					</div>
					<div class="form-group">
						<label for="QUESTION_DEFAULT_ANSWER">Default Answer</label>
						<input type="text" class="form-control" name="QUESTION_DEFAULT_ANSWER" id="QUESTION_DEFAULT_ANSWER" placeholder="Lorem ipsum dollinar." value="{defaultAnswer}" />
					</div>					
					<div class="form-group">
						<label for="QUESTION_REQUIRED">Required</label>
						<div class="radio first">
							<label>
								<input type="radio" name="QUESTION_REQUIRED" id="QUESTION_REQUIRED_TRUE" value="true">
									<xsl:if test="required = 'true'">
										<xsl:attribute name="checked">checked</xsl:attribute>
									</xsl:if>
								</input>
								<xsl:text>Yes</xsl:text>
							</label>
						</div>
						<div class="radio">
							<label>
								<input type="radio" name="QUESTION_REQUIRED" id="QUESTION_REQUIRED_FALSE" value="false">
									<xsl:if test="required = 'false'">
										<xsl:attribute name="checked">checked</xsl:attribute>
									</xsl:if>
								</input>
								<xsl:text>No</xsl:text>
							</label>
						</div>
					</div>
					<div class="form-group">
						<label for="QUESTION_FILTER">Filter</label>
						<div class="radio first">
							<label>
								<input type="radio" name="QUESTION_FILTER" id="QUESTION_FILTER_NONE" value="none">
									<xsl:if test="filter = 'none'">
										<xsl:attribute name="checked">checked</xsl:attribute>
									</xsl:if>
								</input>
								<xsl:text>None</xsl:text>
							</label>
						</div>
						<div class="radio">
							<label>
								<input type="radio" name="QUESTION_FILTER" id="QUESTION_FILTER_DATE" value="date" disabled="disabled">
									<xsl:if test="filter = 'date'">
										<xsl:attribute name="checked">checked</xsl:attribute>
									</xsl:if>
								</input>
								<xsl:text>Date</xsl:text>
							</label>
						</div>
						<div class="radio">
							<label>
								<input type="radio" name="QUESTION_FILTER" id="QUESTION_FILTER_EMAIL" value="email" disabled="disabled">
									<xsl:if test="filter = 'email'">
										<xsl:attribute name="checked">checked</xsl:attribute>
									</xsl:if>
								</input>
								<xsl:text>Email</xsl:text>
							</label>
						</div>
						<div class="radio">
							<label>
								<input type="radio" name="QUESTION_FILTER" id="QUESTION_FILTER_PHONE" value="phone" disabled="disabled">
									<xsl:if test="filter = 'phone'">
										<xsl:attribute name="checked">checked</xsl:attribute>
									</xsl:if>
								</input>
								<xsl:text>Phone Number</xsl:text>
							</label>
						</div> 
					</div>
					<div class="btn-toolbar">
						<a class="btn btn-success" href="javascript:saveQuestion();">Save</a>
						<a class="btn btn-danger" href="javascript:switchTab('QUESTION_LIST');">Cancel</a>
					</div>
				</xsl:for-each>
			</div>
			<!-- survey sidebar -->
			<div class="col-lg-4">
				<ul class="nav nav-pills nav-stacked">
					<li role="presentation" class="disabled"><a href="javascript:switchTab('GENERAL');">General</a></li>
					<li role="presentation" class="active"><a href="#">Questions</a></li>
				</ul>
			</div>
			</div>
		</form>
	</xsl:template>
</xsl:stylesheet>
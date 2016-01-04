<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
		<form action="" method="post" name="portal_form">
			<input type="hidden" name="ACTION" />
			<input type="hidden" name="SCREEN">
				<xsl:attribute name="value">
					<xsl:choose>					
						<xsl:when test="/data/form/question/type = 'text'">QUESTION_TYPE_TEXT</xsl:when>
						<xsl:when test="/data/form/question/type = 'textarea'">QUESTION_TYPE_TEXTAREA</xsl:when>
					</xsl:choose>
				</xsl:attribute>
			</input>
			<input type="hidden" name="QUESTION_TYPE">
				<xsl:attribute name="value">
					<xsl:choose>					
						<xsl:when test="/data/form/question/type = 'text'">text</xsl:when>
						<xsl:when test="/data/form/question/type = 'textarea'">textarea</xsl:when>
					</xsl:choose>
				</xsl:attribute>
			</input>
			<input type="hidden" name="FORM_ID" value="{/data/form/id}" />
			<input type="hidden" name="QUESTION_ID" value="{/data/form/question/id}" />
			<!-- survey content -->
			<div class="row">
				<div class="col-lg-12">
					<nav>
						<ul class="nav nav-pills">
							<xsl:choose>							
								<xsl:when test="/data/form/question/type = 'text'">
									<li role="presentation" class="active"><a href="#">Text</a></li>
									<li role="presentation"><a href="javascript:switchTab('QUESTION_TYPE_TEXTAREA');">Textarea</a></li>
								</xsl:when>
								<xsl:when test="/data/form/question/type = 'textarea'">							
									<li role="presentation"><a href="javascript:switchTab('QUESTION_TYPE_TEXT');">Text</a></li>
									<li role="presentation" class="active"><a href="#">Textarea</a></li>
								</xsl:when>
							</xsl:choose>
							<li role="presentation"><a href="javascript:switchTab('QUESTION_TYPE_RADIO');">Radio</a></li>
							<li role="presentation"><a href="javascript:switchTab('QUESTION_TYPE_CHECKBOX');">Checkbox</a></li>
							<li role="presentation"><a href="javascript:switchTab('QUESTION_TYPE_PULLDOWN');">Pulldown</a></li>
						</ul>
					</nav>
					<h2 class="screen-title"><xsl:value-of select="/data/form/title" /> <small><xsl:value-of select="/data/form/question/type" /></small></h2>
					<xsl:for-each select="/data/form/question">
						<div class="form-group">
							<label for="QUESTION_LABEL">Label</label>
							<input type="text" class="form-control" name="QUESTION_LABEL" id="QUESTION_LABEL" value="{label}" />
						</div>
						<div class="form-group">
							<label for="QUESTION_DEFAULT_ANSWER">Default Answer</label>
							<xsl:choose>
								<xsl:when test="type = 'text'">
									<input type="text" class="form-control" name="QUESTION_DEFAULT_ANSWER" id="QUESTION_DEFAULT_ANSWER" placeholder="Lorem ipsum dollinar." value="{defaultAnswer}" />
								</xsl:when>
								<xsl:when test="type = 'textarea'">							
									<textarea class="form-control" name="QUESTION_DEFAULT_ANSWER" id="QUESTION_DEFAULT_ANSWER" placeholder="Lorem ipsum dollinar.">
										<xsl:choose>
											<xsl:when test="string-length(defaultAnswer) = 0">
												<xsl:text>&#x0A;</xsl:text>
											</xsl:when>
											<xsl:otherwise>
												<xsl:value-of select="defaultAnswer" />
											</xsl:otherwise>
										</xsl:choose>
									</textarea>
								</xsl:when>
							</xsl:choose>
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
						<xsl:if test="type = 'text'">
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
										<input type="radio" name="QUESTION_FILTER" id="QUESTION_FILTER_DATE" value="date">
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
						</xsl:if>
						<div class="btn-toolbar">
							<a class="btn btn-success" href="javascript:saveQuestion();">Save</a>
							<a class="btn btn-danger" href="javascript:switchTab('QUESTION_LIST');">Back to Questions</a>
						</div>
					</xsl:for-each>
				</div>
			</div>
		</form>
	</xsl:template>
</xsl:stylesheet>
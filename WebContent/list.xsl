<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
		<form action="" method="post" name="portal_form">
			<input type="hidden" name="ACTION" />
			<input type="hidden" name="SCREEN" value="FORM_LIST" />
			<input type="hidden" name="FORM_ID" />
			<div class="btn-group">
				<a class="btn btn-default" href="javascript:createForm();"><span class="fa fa-plus"><span class="hide">Plus</span></span> Create New Form</a>
				<a class="btn btn-default"><span class="fa fa-trash"><span class="hide">Delete Form(s)</span></span> Delete Forms</a>
			</div>
			<span class="pull-right text-danger user-info">Hello, <xsl:value-of select="concat(/data/user/firstName, ' ', /data/user/lastName)" /></span>
			<hr />
			<table class="table table-bordered table-condensed table-striped">
				<thead>
					<tr>
						<th class="col-lg-1 text-center">ID</th>
						<th>Title</th>
						<th class="col-lg-1 text-center">View</th>
						<th class="col-lg-1 text-center">Edit</th>
						<th class="col-lg-1 text-center">Delete</th>
					</tr>
				</thead>
				<tbody>
					<xsl:for-each select="/data/form">
						<tr>
							<th class="text-center"><input type="checkbox" name="FORM_ID_LIST" value="{id}" /></th>
							<td><xsl:value-of select="title" /></td>
							<td class="text-center"><a href="/webform/public/{id}" target="_blank"><span class="fa fa-search" /></a></td>
							<td class="text-center"><a href="javascript:editForm('{id}');"><span class="fa fa-edit" /></a></td>
							<td class="text-center"><a href="javascript:deleteForm('{id}');"><span class="fa fa-trash" /></a></td>
						</tr>
					</xsl:for-each>
				</tbody>
			</table>
		</form>
	</xsl:template>
</xsl:stylesheet>
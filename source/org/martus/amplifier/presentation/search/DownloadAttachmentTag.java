package org.martus.amplifier.presentation.search;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.martus.amplifier.common.configuration.AmplifierConfiguration;
import org.martus.amplifier.service.attachment.AttachmentManager;
import org.martus.amplifier.service.attachment.AttachmentStorageException;
import org.martus.amplifier.service.attachment.filesystem.FileSystemAttachmentManager;
import org.martus.util.StreamCopier;

public class DownloadAttachmentTag extends TagSupport
{
	public DownloadAttachmentTag()
	{
		AmplifierConfiguration config = 
			AmplifierConfiguration.getInstance();
		indexPath = config.getBasePath();
	}

	public int doEndTag() throws JspException 
	{
// This is JSP-specific
//		String accountId = (String) ExpressionEvaluatorManager.evaluate(
//			"accountId", accountIdEL, String.class, this, pageContext);
//		String localId = (String) ExpressionEvaluatorManager.evaluate(
//			"localId", localIdEL, String.class, this, pageContext);
			
		AttachmentManager attachmentManager = null;
		InputStream in = null;
		
		try {		
			attachmentManager = openAttachmentManager();
//			UniversalId attachmentId = 
//				UniversalId.createFromAccountAndLocalId(accountId, localId);
//			in = attachmentManager.getAttachment(attachmentId);
			OutputStream out = pageContext.getResponse().getOutputStream();
			new StreamCopier().copyStream(in, out);
		} catch (AttachmentStorageException e) {
			throw new JspException(e);
		} catch (IOException e) {
			throw new JspException(e);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				throw new JspException(
					"Unable to close attachment input stream", e);
			} finally {
				if (attachmentManager != null) {
					try {
						attachmentManager.close();
					} catch (AttachmentStorageException e) {
						throw new JspException(
							"Unable to close attachment manager", e);
					}
				}
			}
		}
		
		return super.doEndTag();
	}
	
	private AttachmentManager openAttachmentManager() 
		throws AttachmentStorageException
	{
		return new FileSystemAttachmentManager(indexPath);
	}
	
	private String indexPath;
}
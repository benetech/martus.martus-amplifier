package org.martus.amplifier.presentation.search;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.taglibs.standard.lang.support.ExpressionEvaluatorManager;
import org.martus.amplifier.common.configuration.AmplifierConfiguration;
import org.martus.amplifier.service.attachment.AttachmentManager;
import org.martus.amplifier.service.attachment.AttachmentStorageException;
import org.martus.amplifier.service.attachment.filesystem.FileSystemAttachmentManager;
import org.martus.common.packet.UniversalId;
import org.martus.util.StreamCopier;

public class DownloadAttachmentTag extends TagSupport
{
	public DownloadAttachmentTag()
	{
		AmplifierConfiguration config = 
			AmplifierConfiguration.getInstance();
		indexPath = config.getBasePath();
	}
	
	public void setAccountId(String accountId)
	{
		accountIdEL = accountId;
	}
	
	public void setLocalId(String localId)
	{
		localIdEL = localId;
	}

	public int doEndTag() throws JspException 
	{
		String accountId = (String) ExpressionEvaluatorManager.evaluate(
			"accountId", accountIdEL, String.class, this, pageContext);
		String localId = (String) ExpressionEvaluatorManager.evaluate(
			"localId", localIdEL, String.class, this, pageContext);
			
		AttachmentManager attachmentManager = null;
		InputStream in = null;
		
		try {		
			attachmentManager = openAttachmentManager();
			UniversalId attachmentId = 
				UniversalId.createFromAccountAndLocalId(accountId, localId);
			in = attachmentManager.getAttachment(attachmentId);
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
	
	private String accountIdEL;
	private String localIdEL;
	private String indexPath;
	
	private static final Logger LOG = 
		Logger.getLogger("DownloadAttachmentTag");

	
}
/**
 * Copyright (c) 2017 NumberFour AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   NumberFour AG - Initial API and implementation
 */
package org.eclipse.n4js.ui.editor;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.ISourceViewerAware;
import org.eclipse.xtext.ui.editor.hyperlinking.DefaultHyperlinkDetector;
import org.eclipse.xtext.ui.editor.hyperlinking.IHyperlinkHelper;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;

/**
 * Minor customization of {@link DefaultHyperlinkDetector} to avoid blocking the UI thread.
 */
public class N4JSHyperlinkDetector extends DefaultHyperlinkDetector {

	/**
	 * Method copied from super class with only a minor change: call to "readOnly" changed to "tryReadOnly".
	 */
	@Override
	public IHyperlink[] detectHyperlinks(final ITextViewer textViewer, final IRegion region,
			final boolean canShowMultipleHyperlinks) {
		final IDocument xtextDocument = textViewer.getDocument();
		if (!(xtextDocument instanceof N4JSDocument)) {
			return super.detectHyperlinks(textViewer, region, canShowMultipleHyperlinks);
		}
		final IHyperlinkHelper helper = getHelper();
		return ((N4JSDocument) xtextDocument).tryReadOnly(new IUnitOfWork<IHyperlink[], XtextResource>() {
			@Override
			public IHyperlink[] exec(XtextResource resource) throws Exception {
				if (resource == null) {
					return null;
				}
				if (helper instanceof ISourceViewerAware && textViewer instanceof ISourceViewer) {
					((ISourceViewerAware) helper).setSourceViewer((ISourceViewer) textViewer);
				}
				return helper.createHyperlinksByOffset(resource, region.getOffset(), canShowMultipleHyperlinks);
			}
		}, null);
	}
}

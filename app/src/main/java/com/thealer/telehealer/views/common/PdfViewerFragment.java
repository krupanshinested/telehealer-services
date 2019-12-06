package com.thealer.telehealer.views.common;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.AppBarLayout;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.barteksc.pdfviewer.PDFView;
import com.thealer.telehealer.R;
import com.thealer.telehealer.apilayer.models.FileSavedInterface;
import com.thealer.telehealer.apilayer.models.PdfReceiverApiViewModel;
import com.thealer.telehealer.common.ArgumentKeys;
import com.thealer.telehealer.common.PrintDocumentManager;
import com.thealer.telehealer.views.base.BaseFragment;

import java.io.File;

/**
 * Created by Aswin on 27,November,2018
 */
public class PdfViewerFragment extends BaseFragment implements View.OnClickListener {
    private ImageView backIv;
    private TextView toolbarTitle;
    private Button printBt;

    private PdfReceiverApiViewModel pdfReceiverApiViewModel;
    private AttachObserverInterface attachObserverInterface;
    private PDFView pdfView;
    private OnCloseActionInterface onCloseActionInterface;
    private ChangeTitleInterface changeTitleInterface;
    private Toolbar toolbar;
    private File resultfile;
    private String title;
    private AppBarLayout appbarLayout;
    private WebView webView;
    private String htmlFile;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        onCloseActionInterface = (OnCloseActionInterface) getActivity();
        attachObserverInterface = (AttachObserverInterface) getActivity();
        pdfReceiverApiViewModel = new ViewModelProvider(this).get(PdfReceiverApiViewModel.class);
        attachObserverInterface.attachObserver(pdfReceiverApiViewModel);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pdf_viewer, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        backIv = (ImageView) view.findViewById(R.id.back_iv);
        toolbarTitle = (TextView) view.findViewById(R.id.toolbar_title);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        printBt = (Button) view.findViewById(R.id.print_bt);
        pdfView = (PDFView) view.findViewById(R.id.pdf_view);
        webView = (WebView) view.findViewById(R.id.webView);

        backIv.setOnClickListener(this);
        printBt.setOnClickListener(this);
        printBt.setEnabled(false);

        if (getArguments() != null) {

            boolean isFromDetail = getArguments().getBoolean(ArgumentKeys.IS_FROM_PRESCRIPTION_DETAIL);
            boolean isPdfDecrypt = getArguments().getBoolean(ArgumentKeys.IS_PDF_DECRYPT);
            boolean isHideToolbar = getArguments().getBoolean(ArgumentKeys.IS_HIDE_TOOLBAR);

            title = getArguments().getString(ArgumentKeys.PDF_TITLE);
            toolbarTitle.setText(title);

            if (isHideToolbar) {
                toolbar.setVisibility(View.GONE);
                changeTitleInterface = (ChangeTitleInterface) getActivity();
                changeTitleInterface.onTitleChange(title);
            }

            if (getArguments().getString(ArgumentKeys.HTML_FILE) != null) {
                htmlFile = getArguments().getString(ArgumentKeys.HTML_FILE);
                loadPdf(htmlFile);
            } else {
                String fileUrl = getArguments().getString(ArgumentKeys.PDF_URL);
                pdfReceiverApiViewModel.getPdfFile(fileUrl, isPdfDecrypt,
                        new FileSavedInterface() {
                            @Override
                            public void onFileSaved(File file) {
                                resultfile = file;
                                loadPdf(file);
                            }
                        });
            }
        }
    }

    private void loadPdf(String htmlFile) {

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(true);
        webView.loadDataWithBaseURL("file:///android_asset/", htmlFile, "text/html", "UTC-8", null);

        webView.setVisibility(View.VISIBLE);
        printBt.setEnabled(true);
    }

    private void loadPdf(File file) {
        pdfView.fromFile(file)
                .defaultPage(0)
                .enableSwipe(true)
                .load();

        printBt.setEnabled(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_iv:
                onCloseActionInterface.onClose(false);
                break;
            case R.id.print_bt:
                if (htmlFile == null) {

                    PrintDocumentManager
                            .with(getActivity())
                            .print(resultfile, title);
                } else {

                    PrintDocumentManager
                            .with(getActivity())
                            .print(webView, title);
                }
                break;
        }
    }
}

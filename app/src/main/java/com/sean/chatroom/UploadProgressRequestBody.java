package com.sean.chatroom;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

public class UploadProgressRequestBody extends RequestBody {
    private RequestBody mRequestBody;
    private FileUploadObserver<ResponseBody> fileUploadObserver;
    private File file;

    public UploadProgressRequestBody(File file, FileUploadObserver<ResponseBody> fileUploadObserver) {
        this.file = file;
        this.mRequestBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
        this.fileUploadObserver = fileUploadObserver;
    }

    @Override
    public MediaType contentType() {
        return mRequestBody.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return mRequestBody.contentLength();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        long max = contentLength();
        Source source = Okio.source(file);
        Buffer buffer = new Buffer();
        long length ;
        long sum = 0;

        while ((length = source.read(buffer, 1024)) != -1) {
            sink.write(buffer, length);
            sum += length;
            fileUploadObserver.onProgressChange(sum, max);
        }
        buffer.flush();
    }
}
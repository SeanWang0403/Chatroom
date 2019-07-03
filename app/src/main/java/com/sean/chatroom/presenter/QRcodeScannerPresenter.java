package com.sean.chatroom.presenter;

import android.content.Context;

import com.sean.chatroom.bean.FriendItem;
import com.sean.chatroom.model.QRcodeScannerModel;
import com.sean.chatroom.view.QRcodeScannerView;

public class QRcodeScannerPresenter implements QRcodeScannerModel.QRcodeScanneListener {
    private QRcodeScannerModel qRcodeScannerModel;
    private QRcodeScannerView qRcodeScannerView;

    public QRcodeScannerPresenter(QRcodeScannerView qRcodeScannerView, Context context) {
        this.qRcodeScannerView = qRcodeScannerView;
        this.qRcodeScannerModel = new QRcodeScannerModel(context);
    }

    public String getUserID() {
        return qRcodeScannerModel.getUserID();
    }

    public void SearchFriend(String qrcode) {
        qRcodeScannerModel.SearchFriend(qrcode, this);
    }

    public void addNewFriend(String myID, FriendItem friendItem) {
        qRcodeScannerModel.addNewFriend(myID, friendItem, this);
    }

    @Override
    public void FriendData(boolean myself, boolean exist, FriendItem friendItem) {
        qRcodeScannerView.FriendData(myself, exist, friendItem);
    }

    @Override
    public void addNewFriendSuccess() {
        qRcodeScannerView.addNewFiendSuccess();
    }

    @Override
    public void SearchError() {
        qRcodeScannerView.SearchError();
    }

}

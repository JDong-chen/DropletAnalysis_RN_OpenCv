import React from 'react'
import {
    View,
    TouchableOpacity,
    Image,

} from 'react-native';

import {
    NavigationPage,
    Label,
    PullPicker,
    ModalIndicator
} from 'teaset';
import ImagePicker2 from 'react-native-image-picker'
import DAImage from './Pages/DAImage'

const options = {
  title: '请选择水敏纸雾滴分布照片',
  storageOptions: {
    skipBackup: true,
    path: 'images',
  },
  chooseFromLibraryButtonTitle:'从相册选取',
  takePhotoButtonTitle:'拍摄',
  cancelButtonTitle: '取消'
};



class Home extends NavigationPage {

  static defaultProps = {
    ...NavigationPage.defaultProps,
    title: 'DropletAnalysis',
    showBackButton: false,
  }

  renderPage() {
    return (
      <View style={{flex: 1, alignItems: 'center', justifyContent: 'center'}}>
        <TouchableOpacity onPress={this._onPressButton.bind(this)}>
           <Image source={require('../app_logo.png')} />
        </TouchableOpacity>
        <Label type='detail' size='xl' text={"点击开始"} />
      </View>
    )
  }

  _onPressButton() {

                ImagePicker2.showImagePicker(options, (response) => {

                  if (response.didCancel) {
                    console.log('User cancelled image picker')
                  } else if (response.error) {
                    console.log('ImagePicker Error: ', response.error)
                  } else if (response.customButton) {

                  } else {
//                  console.log(response.fileName)
                    this.navigator.push({ view:<DAImage img={ response   }/> })
                  }
                })
               }




}


export default Home


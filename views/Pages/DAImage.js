import React from 'react';
import {Alert, Dimensions, StyleSheet, View, TouchableOpacity,Text,ImageBackground,Image} from 'react-native'

import {ModalIndicator, Toast, BasePage,PullPicker } from 'teaset'


import Result from './Result';

import OpenCV from '../opencv/opencv'


const {height,width} =  Dimensions.get('window')

const selectItems = [
   '图像预处理',
   '图像二值化',
   '边缘检测',
   '绘制轮廓',
   '雾滴分析'
]

export default class DAImage extends BasePage {



  renderPage() {

    return (
      <View style={styles.container}>
         <ImageBackground source={{uri:'data:image/jpg;base64,'+this.props.img.data }} width={ this.props.img.width }
                          height={ this.props.img.height }  resizeMode={'contain'} style={styles.bgImg} >
            <View style={{ flex: 0, flexDirection: 'row', justifyContent: 'space-around',width:width }}>

                <TouchableOpacity onPress={this.selectOpt.bind(this)} style={styles.capture}>
                   <Image style={styles.cameraImage} source={require('../../images/opt.png')} />
                </TouchableOpacity>

            </View>
          </ImageBackground>

      </View>
    );
  }
    selectOpt(){
        PullPicker.show(
            '请选择操作',
             selectItems,
             0,
             (item, index) => {
                if(index === 0) {
                    ModalIndicator.show('正在处理')
                    setTimeout(()=> {
                        ModalIndicator.hide()
                        Toast.success('处理完毕')
                     },1500)

                     //图像预处理
                    OpenCV.pretreat(this.props.img.path,this.props.img.fileName,this.props.img.data, (error) => {
                              Alert.alert("处理失败,请重试")
                    },
                    (msg,newImg, newPath) => {

                      res = {
                        width: this.props.img.width,
                        height: this.props.img.height,
                        fileName: this.props.img.fileName,
                        data: newImg,
                        path: newPath
                      }
                      this.navigator.push({ view:<DAImage img={ res }/> })
                    }
                    )


                } else if(index === 1) {
                     ModalIndicator.show('正在处理')
                     setTimeout(()=> {
                          ModalIndicator.hide()
                          Toast.success('处理完毕')
                     },1500)

                    //图像二值化
                    OpenCV.thresholding(this.props.img.path,this.props.img.fileName,this.props.img.data, (error) => {
                      Alert.alert(error)
                    },
                    (msg,newImg, newPath) => {

                      res = {
                        width: this.props.img.width,
                        height: this.props.img.height,
                        fileName: this.props.img.fileName,
                        data: newImg,
                        path: newPath
                      }
                      this.navigator.push({ view:<DAImage img={ res }/> })
                    }
                    )

                }else if(index === 2) {
                    ModalIndicator.show('正在处理')
                    setTimeout(()=> {
                       ModalIndicator.hide()
                       Toast.success('处理完毕')
                    },1500)

                    //边缘检测
                    OpenCV.imgCanny(this.props.img.path,this.props.img.fileName,this.props.img.data, (error) => {
                      Alert.alert('处理失败,请重试')
                    },
                    (msg,newImg, newPath) => {

                    let  res = {
                        width: this.props.img.width,
                        height: this.props.img.height,
                        fileName: this.props.img.fileName,
                        data: newImg,
                        path: newPath
                      }
                      this.navigator.push({ view:<DAImage img={ res }/> })
                    }
                    )
                }else if(index === 4) {
                    ModalIndicator.show('正在处理')
                    setTimeout(()=> {
                       ModalIndicator.hide()
                       Toast.success('处理完毕')
                    },1500)

                    //雾滴分析
                    OpenCV.DropletAnalysis(this.props.img.path,this.props.img.fileName,this.props.img.data, (error) => {
                      Alert.alert('处理失败,请重试')
                    },
                    (num,sq1,D,width,height) => {
                       let sq0 = width * height
                      let sq2 = width/28.346*height/28.346*100
                      let res = {
                        num, //数量
                        sq0,//总面积
                        sq1,//雾滴总面积
                        per:sq1/sq0*100,//覆盖率
                        per1:sq1/sq2,//沉积密度
                        D ,//当量直径
                      }
                      this.navigator.push({ view:<Result res = { res }/> })

                    
                    }
                    )
                } else if(index === 3) {
                                    ModalIndicator.show('正在处理')
                                    setTimeout(()=> {
                                       ModalIndicator.hide()
                                       Toast.success('处理完毕')
                                    },1500)

                                    OpenCV.markContour(this.props.img.path,this.props.img.fileName,this.props.img.data, (error) => {
                                      Alert.alert('处理失败,请重试')
                                    },
                                    (msg,newImg, newPath) => {

                                    let res = {
                                        width: this.props.img.width,
                                        height: this.props.img.height,
                                        fileName: this.props.img.fileName,
                                        data: newImg,
                                        path: newPath
                                      }
                                      this.navigator.push({ view:<DAImage img={ res }/> })
                                    }
                                    )

                                  }
             }
        )
    }

}

const styles = StyleSheet.create({
      container: {
        flex: 1,
        flexDirection: 'column',
        backgroundColor: 'black',
      },
      bgImg: {
        width:width,
        height:height,
        flex: 1,
        justifyContent: 'flex-end',
        alignItems: 'center',
      },
      capture: {
        flex: 0,
        backgroundColor: '#ddd',
        borderRadius: 15,
        padding: 0,
        paddingHorizontal:0,
        alignSelf: 'center',
        marginBottom: 20,
      },
      cameraImage: {
        width:50,
        height:50
      }

})
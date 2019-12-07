import React from 'react';
import {View} from 'react-native';

import {NavigationPage, Label, ListRow } from 'teaset';

export default class Result extends NavigationPage {

  static defaultProps = {
    ...NavigationPage.defaultProps,
    title: '雾滴分析结果',
    showBackButton: true,
  };

  renderPage() {
    return (
    <View>
        <ListRow title='雾滴数量(个)' detail= { this.props.res.num }/>
        <ListRow title='雾滴所占面积(像素)' detail= { this.props.res.sq1 }  />
        <ListRow title='图片总面积(像素)' detail= { this.props.res.sq0 }   />
        <ListRow title='雾滴覆盖率(%)' detail= { this.props.res.per }  />
        <ListRow title='沉积密度(个/mm^3)' detail= { this.props.res.per1 }  />
        <ListRow title='平均当量直径(像素)' detail= { this.props.res.D }  />
     </View>

    );
  }

}
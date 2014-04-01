# Ponto [![Build Status](https://buildhive.cloudbees.com/job/mitoma/job/ponto/badge/icon)](https://buildhive.cloudbees.com/job/mitoma/job/ponto/)

Pontoはタイプセーフなプロパティファイルへのアクセサを生成するライブラリです。

## 特徴

タイプセーフなプロパティファイルへのアクセサをaptによって自動生成することにより、
プロパティファイルのキー名の入力間違いなどによるバグをコンパイル時に検出することができます。

また、キー名の最後に _int, _date など型情報を追加することにより文字列以外の定数もアクセサクラスから直接 int や java.util.Date などの型で取得できます。

### Java標準のプロパティファイルへのアクセス方法

```
Properties properties = new Properties();
ClassLoader loader = ClassLoader.getSystemClassLoader();
try{
  properties.load(loader.getResourceAsStream("ponto.properties"));
}catch (Exception e){}
properties.getProperty("setting.key1"); 
// => "value1"
// "setting.key1" が本当に存在するのかわからない。
// （コンパイル時に検出できない）
```

### Pontoを使ったプロパティファイルへのアクセス方法

```
PontoResource.setting.key1();
// => "value1"
// aptにより自動生成された PontoResourceクラスから安全にプロパティを取得できる。
// （存在しないキーを指定した場合コンパイルエラーになる）
```

## 簡単な使い方

### maven に依存関係を追加する

```
  <dependencies>
  	<dependency>
  		<groupId>in.tombo</groupId>
  		<artifactId>ponto</artifactId>
  		<version>1.0.2</version>
  	</dependency>
  </dependencies>
```

### プロパティファイルを用意する。

src/main/resources/ponto.properties

```
setting.key1=value1
setting.key2._int=123
setting.key3._long=123412341234
setting.key4._float=123.123
setting.key5._double=123123.123123
setting.key6._date=2014-01-01
setting.key7._timestamp=2014-01-01 01:02:03
```

### コンフィグファイルを作成し、アノテーションをつける。

src/main/java/your/package/PontoConfig.java

```
@ConstantResource
public class PontoConfig {
}
```

### mvn compile する。

target/generated-sources/annotations に in.tombo.ponto.PontoResource というクラスファイルが生成される。

このクラスを使ってタイプセーフにプロパティファイルにアクセスじゃー！

## アノテーションリファレンス

@ConstantResource に指定できる引数

- value
 - String[] propertiesファイルを複数指定することができる。
 - .properties と .xml 形式の2つのプロパティファイルの書式に対応している。
 - default {"ponto.properties"}
- packageName
 - String 自動生成されるクラスのパッケージを指定
 - default "in.tombo.ponto"
- className
 - String 自動生成されるクラスのクラス名を指定
 - default "PontoResource"
- keyStyle
 - KeyStyle アクセサのスタイルを選択できる。Hierarchical は PontoResource.setting.key1() のようにアクセスできる。 Flat は PontoResource.setting_key1() のようにアクセスできる。
 - default KeyStyle.Hierarchical
- scanPeriod
 - long プロパティファイルをリロードするまでの時間をミリ秒単位で指定する。0以下指定時は再読み込みを行わない。
 - default -1

## ライセンス

MIT-LICENSE

## Pontoの由来

言わずと知れた京都の飲み屋街、[先斗町](http://ja.wikipedia.org/wiki/%E5%85%88%E6%96%97%E7%94%BA)である。

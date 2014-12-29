# Ponto [![Build Status](https://travis-ci.org/mitoma/ponto.svg)](https://travis-ci.org/mitoma/ponto)

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
  		<version>1.0.9</version>
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
setting.key8._boolean=true
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

## 環境毎のプロパティの切り替え

開発時とプロダクションでは、プロパティファイルの値を切り替えたい場合があります。
具体的にはDBの接続IPであるとか、ホスト名とかそういう情報です。
PONTO では PONTO_ENV という環境変数の値で、読み込むプロパティファイルを上書きするプロパティファイルを設定することができます。

例えば setting.properties を読み込んでいる時、 PONTO_ENV に develpment を指定して起動すると setting_development.properties も同時読み込みます。

この時、プロパティの値はsetting_development.properties → setting_properties の順序で解決されます。


## アノテーションリファレンス

@ConstantResource に指定できる引数

<table>
  <tr>
    <th>パラメータ名</th>
    <th>型</th>
    <th>説明</th>
    <th>デフォルト値</th>
  </tr>
  <tr>
    <td>value</td>
    <td>String[]</td>
    <td>propertiesファイルを複数指定することができる。<br/>.properties と .xml 形式の2つのプロパティファイルの書式に対応している。</td>
    <td>{"ponto.properties"}</td>
  </tr>
  <tr>
    <td>packageName</td>
    <td>String</td>
    <td>自動生成されるクラスのパッケージを指定</td>
    <td>"in.tombo.ponto"</td>
  </tr>
  <tr>
    <td>className</td>
    <td>String</td>
    <td>自動生成されるクラスのクラス名を指定</td>
    <td>"PontoResource"</td>
  </tr>
  <tr>
    <td>keyStyle</td>
    <td>KeyStyle</td>
    <td>アクセサのスタイルを選択できる。<br/>Hierarchical は PontoResource.setting.key1() のようにアクセスできる。<br/>Flat は PontoResource.setting_key1() のようにアクセスできる。</td>
    <td>KeyStyle.Hierarchical</td>
  </tr>
  <tr>
    <td>envKey</td>
    <td>String</td>
    <td>上書きするプロパティファイルを決定する時の環境変数名を指定する。</td>
    <td>PONTO_ENV</td>
  </tr>
  <tr>
    <td>envDefault</td>
    <td>String</td>
    <td>上書きするプロパティファイルを決定するときの環境変数が存在しなかった場合のデフォルト値を指定する。</td>
    <td>development</td>
  </tr>
  <tr>
    <td>scanPeriod</td>
    <td>long</td>
    <td>プロパティファイルをリロードするまでの時間をミリ秒単位で指定する。<br/>0以下指定時は再読み込みを行わない。</td>
    <td>-1L</td>
  </tr>
</table>

## ライセンス

MIT-LICENSE

## Pontoの由来

言わずと知れた京都の飲み屋街、[先斗町](http://ja.wikipedia.org/wiki/%E5%85%88%E6%96%97%E7%94%BA)である。

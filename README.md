# Ponto

Pontoはタイプセーフなプロパティファイルへのアクセサを生成するライブラリです。

## 特徴

タイプセーフなプロパティファイルへのアクセサをaptによって自動生成することにより、
プロパティファイルのキー名の入力間違いなどによるバグをコンパイル時に検出することができます。

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
  		<groupId>org.mitoma.ponto</groupId>
  		<artifactId>Ponto</artifactId>
  		<version>0.0.1-SNAPSHOT</version>
  		<scope>provided</scope>
  	</dependency>
  </dependencies>
```

### プロパティファイルを用意する。

src/main/resources/ponto.properties

```
setting.key1=value1
setting.key2=value2
setting.key3=value3
```

### コンフィグファイルを作成し、アノテーションをつける。

src/main/java/your/package/PontoConfig.java

```
@ConstantResource
public class PontoConfig {
}
```

### mvn compile する。

target/generated-sources/annotations に org.mitoma.PontoResource というクラスファイルが生成される。

このクラスを使ってタイプセーフにプロパティファイルにアクセスじゃー！

## アノテーションリファレンス

@ConstantResource に指定できる引数

- value
 - String[] propertiesファイルを複数指定することができる。
 - .properties と .xml 形式の2つのプロパティファイルの書式に対応している。
 - default {"ponto.properties"}
- packageName
 - String 自動生成されるクラスのパッケージを指定
 - default "org.mitoma.ponto"
- className
 - String 自動生成されるクラスのクラス名を指定
 - default "PontoResource"
- keyStyle
 - KeyStyle アクセサのスタイルを選択できる。Hierarchical は PontoResource.setting.key1() のようにアクセスできる。 Flat は PontoResource.setting_key1() のようにアクセスできる。
 - default KeyStyle.Hierarchical

## ライセンス

MIT-LICENSE

## Pontoの由来

言わずと知れた京都の飲み屋街、[先斗町](http://ja.wikipedia.org/wiki/%E5%85%88%E6%96%97%E7%94%BA)である。

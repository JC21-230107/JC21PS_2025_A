# VSCode デバッグ環境変数 設定ガイド

このドキュメントは、Visual Studio Code（VSCode）でJava Spring Bootアプリケーションをデバッグ実行する際に、データベースのパスワードなどの機密情報をソースコードに直接記述することなく、安全に環境変数として設定する手順を説明します。

`application.properties`ファイルにパスワードなどを直接書くと、誤ってGitリポジトリにコミットしてしまう危険性があります。この手順はそのリスクを避けるためのベストプラクティスです。

---

## ステップ1: `launch.json` ファイルの作成

まず、VSCodeのデバッグ設定を管理する`launch.json`ファイルを作成します。

1.  VSCodeの左側にあるアクティビティバーから、**「実行とデバッグ」**アイコン（再生ボタンに虫がついた形のアイコン）をクリックします。

2.  ビューの上部に表示される「**launch.jsonファイルを作成します**」という青いリンクをクリックします。
    *   もし既にファイルが存在する場合は、このリンクは表示されません。その場合は、ビュー上部の歯車アイコン⚙をクリックするか、`.vscode/launch.json`を直接開いてください。

3.  コマンドパレットにデバッガの選択肢が表示されたら、「**Java**」を選択します。

4.  続いて、デバッグ対象のmainクラスを聞かれるので、あなたのプロジェクトのエントリーポイントとなるクラス（`@SpringBootApplication`アノテーションが付与されているクラス、例: `ActivityManagementApplication`）を選択します。

これにより、プロジェクトのルートフォルダ内に`.vscode`というディレクトリが作成され、その中に`launch.json`ファイルが生成されます。

---

## ステップ2: `launch.json` に環境変数を設定する

次に、作成された`launch.json`ファイルを編集して、データベースのユーザー名とパスワードを環境変数として追加します。

1.  `.vscode/launch.json`を開きます。初期状態では以下のような内容になっています。

    ```json
    {
        "version": "0.2.0",
        "configurations": [
            {
                "type": "java",
                "name": "Launch YourApplicationName",
                "request": "launch",
                "mainClass": "com.example.yourproject.YourApplicationName",
                "projectName": "your-project-name"
            }
        ]
    }
    ```

2.  `configurations`配列の中にあるオブジェクトに、`env`というキーを追加し、その中に設定したい環境変数を記述します。Spring Bootは、`application.properties`のキー (`spring.datasource.username`) を、大文字でドットをアンダースコアに置き換えた形式 (`SPRING_DATASOURCE_USERNAME`) で認識します。

    ```json
    {
        "version": "0.2.0",
        "configurations": [
            {
                "type": "java",
                "name": "Launch YourApplicationName",
                "request": "launch",
                "mainClass": "com.example.yourproject.YourApplicationName",
                "projectName": "your-project-name",

                // ↓↓↓ このenvブロックを追記する ↓↓↓
                "env": {
                    "SPRING_DATASOURCE_USERNAME": "R07PS_TEAM_A",
                    "SPRING_DATASOURCE_PASSWORD": "あなたのデータベースパスワードをここに入力"
                }
                // ↑↑↑ ここまでを追記 ↑↑↑
            }
        ]
    }
    ```

---

## ステップ3: デバッグ実行と設定の反映

`launch.json`を保存したら、設定は完了です。

1.  再度「実行とデバッグ」ビューを開きます。
2.  ビュー上部のドロップダウンが、先ほど設定した`"name"`（例: `"Launch YourApplicationName"`）になっていることを確認し、緑色の再生ボタン▶をクリックしてデバッグを開始します。

この方法でアプリケーションを起動すると、Spring Bootは`launch.json`の`env`に設定された値を優先的に読み込みます。

### 推奨: `application.properties`からの機密情報の削除

`launch.json`の設定が完了したら、`src/main/resources/application.properties`からユーザー名とパスワードの行は削除またはコメントアウトすることを強く推奨します。

**変更前 (`application.properties`)**
```properties
spring.datasource.url=jdbc:mysql://192.168.54.220/r07ps_team_a?serverTimezone=Asia/Tokyo&useSSL=false
spring.datasource.username=R07PS_TEAM_A
# spring.datasource.password=... ← もしパスワードの行があれば削除
```

**変更後 (`application.properties`)**
```properties
# ユーザー名とパスワードは環境変数から読み込まれるため、ここには記述しない
spring.datasource.url=jdbc:mysql://192.168.54.220/r07ps_team_a?serverTimezone=Asia/Tokyo&useSSL=false
```

これで、機密情報を誤ってバージョン管理に含めてしまう心配なく、安全に開発を進めることができます。

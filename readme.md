# Senshu-Timetable（My時間割）
このアプリは、かつて専修大学で使用されていたポータルシステム(ActiveCampus)の、学生用Android OS向けクライアントアプリです。
> **ActiveCampusは、2020年3月まで使われていましたが、in Campusに移行されて廃止されました。現在、このアプリは使用できません。**

## ActiveCampusの機能
ActiveCampusには、主に以下の機能がありました。
- 大学の各事務課から、学生にメッセージを配信する「お知らせ」機能
  - メッセージごとに、学生の閲覧状況を把握するための「開封確認」機能
  - メッセージごとの添付ファイル（複数）
- 履修登録した授業科目を参照し、時間割を表示する機能
- 履修登録した授業に対する教室の変更、または休講の内容を閲覧する機能

## このアプリに実装されていた機能
- ログイン情報の保存・自動ログイン機能
- 時間割取得・表示機能
- 「お知らせ」の閲覧機能
  - ポータル側の分類に準じて、お知らせを「個人伝言」「お知らせ」「就職情報」の3つのタブに区別して表示
  - 添付ファイルのダウンロード・自動オープン機能
- ToDo管理機能
  - ToDoには、タイトル、期限、時間割取得機能によって取得した教科を設定できます。

## 使用した技術
- Android SDK (API Level 26)
- Jsoup 1.12.2 (HTTPクライアント+HTMLパーサライブラリ)
- Realm 2.0.0 -&gt;10.15.1 (クラスベースDB)

# Othello-netprog
ネットワーク演習（Java演習）課題

## プロジェクト構成

- `scripts/` : サーバー／クライアント起動用のバッチ（`start_server.bat`, `start_client.bat`）
- `src/client/controller` : 盤面操作や通信を制御するコントローラー群
- `src/client/view` : Swing 製 GUI と各種アセット
- `src/common` : 共有プロトコルやコマンド定義
- `src/model` : ボード・セル・石などゲームロジック
- `src/server` : マッチングと対局を管理するサーバーコード

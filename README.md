# othello-netprog

ネットワーク市システム開発演習（Java演習） - 授業課題
Java SwingとSocket通信を使用した、ネットワーク対戦型オセロゲームです。
クライアント・サーバーアーキテクチャを採用しており、複数クライアント間でのリアルタイム対戦が可能です。

## 特徴

- **オンライン対戦**: サーバーを介したリアルタイムマッチングと対局
- **可変ボードサイズ**: 6x6, 8x8, 10x10, 12x12 の盤面サイズに対応
- **GUI**: Java Swingによるリッチなグラフィカルインターフェース
- **アシスト機能**: 石を置ける場所のハイライト表示
- **拡張性**: MVCアーキテクチャに基づいた設計

## 動作環境

- Java Development Kit (JDK) 8 以上
- Windows / macOS / Linux

## 🚀 実行方法

プロジェクトには起動用のバッチスクリプトが含まれています。

### 1. サーバーの起動

まず対戦サーバーを立ち上げます。デフォルトではポート `10000` で待機します。

```cmd
scripts\start_server.bat
````

### 2. クライアントの起動

別のターミナル（またはダブルクリック）でクライアントを起動します。対戦するには少なくとも2つのクライアントが必要です。

```cmd
scripts\start_client.bat
```

### 3. ゲームの流れ

1. クライアント起動後、ロード画面を経てホーム画面が表示されます。
2. **START** ボタンを押すとマッチング設定画面が開きます。
3. **User Name**（プレイヤー名）を入力し、**Board Size**（盤面サイズ）を選択して **Start** を押します。
4. サーバー側で同じ盤面サイズを選択したプレイヤーが見つかると、自動的にゲームが開始されます。

## 📂 ディレクトリ構成

```text
othello-netprog/
├── scripts/                 # 起動用スクリプト
│   ├── start_server.bat     # サーバー起動 (コンパイル込み)
│   └── start_client.bat     # クライアント起動 (コンパイル込み)
├── src/
│   ├── client/              # クライアント側ソースコード
│   │   ├── assets/          # 画像リソース (背景, アイコン, 駒など)
│   │   ├── controller/      # ゲーム進行・通信制御
│   │   ├── view/            # GUI (Swing Panels)
│   │   └── Main.java        # クライアントエントリーポイント
│   ├── server/              # サーバー側ソースコード
│   │   ├── ClientHandler.java
│   │   ├── GameRoom.java    # 対局ロジック管理
│   │   └── OthelloServer.java
│   ├── model/               # 共通モデル (Board, Piece)
│   └── common/              # 通信プロトコル・定数定義
└── README.md
```

## 🛠 技術仕様

### 通信プロトコル

クライアントとサーバーはテキストベースの独自プロトコルで通信します。
詳細は `src/common/Protocol.java` および `CommandType.java` を参照してください。

- `CONNECT <name> <size>`: 接続要求
- `GAME_START <color>`: ゲーム開始通知
- `MOVE <i> <j>`: 着手
- `MOVE_ACCEPTED <i> <j>`:着手受理（盤面更新）
- `YOUR_TURN` / `OPPONENT_TURN`: ターン通知
- `GAME_OVER <result> <w_count> <b_count>`: 終了通知

### 設計

- **View**: `BaseBackgroundPanel` を継承した各パネルにより、背景描画の共通化とリソース管理の効率化を行っています。
- **Model**: `Board` クラスは `Piece` 列挙型を使用し、効率的な盤面管理と石の反転ロジックを提供します。
- **Network**: 別スレッドでの非同期受信を行い、`SwingUtilities.invokeLater` を用いて安全にUIを更新しています。

import { useState } from 'react';
import { CopyIcon, KeyIcon, LinkIcon, BotIcon } from '../util/chatIcons';

export function ConfigurationScreen({
  tokenDraft,
  error,
  suggestedUrl,
  onTokenDraftChange,
  onSubmit,
  onCopyUrl
}: {
  tokenDraft: string;
  error: string | null;
  suggestedUrl: string;
  onTokenDraftChange: (value: string) => void;
  onSubmit: () => void;
  onCopyUrl: () => void;
}) {
  const [useLocalModel, setUseLocalModel] = useState(true);

  return (
    <div className="chat-connection-overlay">
      <section className="configuration-screen" role="dialog">
        <div className="configuration-screen-header">
          <span>Surlor AI 游戏开发工作室</span>
          <h1>🎮 开始创作游戏</h1>
          <p>使用内置 AI 模型直接生成 H5 / Compose 游戏，或连接手机端同步开发会话。</p>
        </div>

        <div className="configuration-screen-block configuration-screen-mode">
          <label className="configuration-screen-label">
            <BotIcon size={16} />
            <span>工作模式</span>
          </label>
          <div className="configuration-screen-mode-options">
            <button
              className={useLocalModel ? 'is-active' : ''}
              onClick={() => setUseLocalModel(true)}
              type="button"
            >
              内置模型
              <small>本地推理，无需 API</small>
            </button>
            <button
              className={!useLocalModel ? 'is-active' : ''}
              onClick={() => setUseLocalModel(false)}
              type="button"
            >
              连接手机
              <small>同步 Operit 会话</small>
            </button>
          </div>
        </div>

        {!useLocalModel && (
          <>
            <div className="configuration-screen-block">
              <label className="configuration-screen-label" htmlFor="web-chat-url">
                <LinkIcon size={16} />
                <span>访问地址</span>
              </label>
              <div className="configuration-screen-inline-card">
                <code id="web-chat-url">{suggestedUrl}</code>
                <button onClick={onCopyUrl} type="button">
                  <CopyIcon size={16} />
                </button>
              </div>
            </div>

            <div className="configuration-screen-block">
              <label className="configuration-screen-label" htmlFor="web-chat-token">
                <KeyIcon size={16} />
                <span>Bearer Token（可选）</span>
              </label>
              <input
                id="web-chat-token"
                onChange={(event) => onTokenDraftChange(event.target.value)}
                placeholder="留空则使用内置模型，填写后连接手机端"
                type="password"
                value={tokenDraft}
              />
            </div>
          </>
        )}

        {error ? <div className="chat-inline-error is-card-error">{error}</div> : null}

        <button className="configuration-screen-submit" onClick={onSubmit} type="button">
          {useLocalModel ? '进入游戏开发工作室' : '连接网页聊天'}
        </button>
      </section>
    </div>
  );
}

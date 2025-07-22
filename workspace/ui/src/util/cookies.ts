// Cookieのキー名

/** リクエストパラメーターR */
const REQUEST_PARAMETER_R = 'r'

/** 認証トークン */
const Auth_Token = 'AuthToken'

/** 遷移用URL */
const MOVE_URL = 'MoveURL'

/** 本部フラグ 0：MRユーザ、1：本部ユーザ */
const HONBU_FLAG = 'honbu_flag'

/** パスワード変更URL */
const PASSWORD_CHANGE_URL = 'PasswordChangeURL'

/** ログアウトURL */
const LOGOUT_URL = 'LogoutURL'

/** CSRF対策トークン */
const XSRF_TOKEN = 'XSRF-TOKEN'
/** APIが呼び出されるたびに自動で有効期限を30分更新するCookieのキー名 */
const AUTO_EXTENSION_30MIN_COOKIE = [
  HONBU_FLAG,
  REQUEST_PARAMETER_R,
  PASSWORD_CHANGE_URL,
]

/** APIが呼び出されるたびに自動で有効期限を1日更新するCookieのキー名 */
const AUTO_EXTENSION_1DAY_COOKIE = [MOVE_URL, LOGOUT_URL]

/**
 * リクエストパラメータRを取得する。
 * @param requestparameter リクエストパラメータ
 */
export function setRequestParameterR(requestParameter: string): void {
  setValue1Day(REQUEST_PARAMETER_R, requestParameter)
}

/**
 * リクエストパラメータRを設定する。
 * @returns requestparameter リクエストパラメータ
 */
export function getRequestParameterR(): string {
  return getValue(REQUEST_PARAMETER_R)
}

/**
 * 本部フラグを取得する。
 * @returns honbu_flag 本部フラグ
 */
export function getHonbuFlag(): string {
  return getValue(HONBU_FLAG)
}

/**
 * MoveURLを設定する。
 * @param url MoveURL
 */
export function setMoveURL(url: string): void {
  setValue1Day(MOVE_URL, url)
}

/**
 * MoveURLを取得する。
 * @returns url MoveURL
 */
export function getMoveURL(): string {
  return getValue(MOVE_URL)
}

/**
 * パスワード変更URLを設定する。
 * @param url PasswordChangeURL
 */
export function setPasswordChangeURL(url: string): void {
  setValue1Day(PASSWORD_CHANGE_URL, url)
}

/**
 * パスワード変更URLを取得する。
 * @param url PasswordChangeURL
 */
export function getPasswordChangeURL(): string {
  return getValue(PASSWORD_CHANGE_URL)
}

/**
 * ログアウトURLを設定する。
 * @param url LogoutURL
 */
export function setLogoutURL(url: string): void {
  setValue1Day(LOGOUT_URL, url)
}

/**
 * ログアウトURLを取得する。
 * @param url LogoutURL
 */
export function getLogoutURL(): string {
  const key = LOGOUT_URL
  let value = getCookieArray().find((cookie) => {
    return cookie.startsWith(`${key}=`)
  })
  if (value) {
    return value.replace('LogoutURL=', '')
  }
  return ''
}

/**
 * 認証情報(AuthToken)を設定する。
 * @param authToken 認証情報
 */
export function setAuth(authToken: string): void {
  setValue1Day(Auth_Token, authToken)
}

/**
 * 認証情報(AuthToken)を取得する
 * @returns 認証情報(AuthToken)
 */
export function getAuth(): string {
  return getValue(Auth_Token)
}

/**
 * CSRF対策トークンを取得する
 * @returns CSRF対策トークン
 */
export function getXsrfToken(): string {
  return getValue(XSRF_TOKEN)
}

/**
 * ログアウト時のCookie削除処理
 */
export function deleteCookiesWhenLogout(): void {
  getCookieArray().forEach((element) => {
    const key = element.split('=')[0]
    deleteCookie(key)
  })
}

/**
 * 有効期限自動更新対象のすべてのCookieの有効期限を更新
 */
export function autoUpdateCookies(): void {
  updateCookies30Minutes()
  updateCookies1Day()
}

/**
 * 有効期限30分自動更新対象のすべてのCookieの有効期限を30分後に更新
 */
export function updateCookies30Minutes(): void {
  getCookieArray().forEach((element) => {
    const key = element.split('=')[0]
    if (AUTO_EXTENSION_30MIN_COOKIE.includes(key)) {
      const value = getValue(key)

      // AUTHの有効期限（30分）よりも確実に長くなるように、31分で設定
      setValueWithExpireMinutes(key, value, 31)
    }
  })
}

/**
 * 有効期限1日自動更新対象のすべてのCookieの有効期限を1日後に更新
 */
export function updateCookies1Day(): void {
  getCookieArray().forEach((element) => {
    const key = element.split('=')[0]
    if (AUTO_EXTENSION_1DAY_COOKIE.includes(key)) {
      let value = ''

      // ログアウトURLの場合は、「=」を含めるようにする
      if (key === LOGOUT_URL) {
        value = getValueIncludingEqual(key)
      } else {
        value = getValue(key)
      }

      setValue1Day(key, value)
    }
  })
}

/**
 * 指定されたkeyの値を返す。存在しない場合は空文字を返す。
 * @param key 取得したいCookieのkey
 * @returns 取得したいCookieのkeyの値
 */
export function getValue(key: string): string {
  let value = getCookieArray().find((cookie) => {
    return cookie.startsWith(`${key}=`)
  })
  value = value !== undefined ? value.split('=')[1] : ''
  return value
}

/**
 * 指定されたkeyの値を返す。存在しない場合は空文字を返す。（valueに=が含まれていた場合を考慮）
 * @param key 取得したいCookieのkey
 * @returns 取得したいCookieのkeyの値
 */
export function getValueIncludingEqual(key: string): string {
  let value = getCookieArray().find((cookie) => {
    return cookie.startsWith(`${key}=`)
  })

  if (value === undefined) {
    return ''
  }

  const match = value.match(new RegExp(`^${key}=(.*)$`))
  return match ? match[1] : ''
}

/**
 * Cookieをkey毎に分割した配列を取得する
 * @returns Cookieの登録key-value配列
 */
function getCookieArray(): string[] {
  return document.cookie.replace(/\s+/g, '').split(';')
}

// #region Cookie設定

/**
 * Cookie設定（有効期限1日）
 * @param key Cookieのkey
 * @param value Cookieのvalue
 */
function setValue1Day(key: string, value: string): void {
  const expireDate: Date = new Date()
  expireDate.setDate(expireDate.getDate() + 1)
  setValueByDate(key, value, expireDate)
}

/**
 * Cookie設定（有効期限日数指定）
 * @param key Cookieのkey
 * @param value Cookieのvalue
 * @param days 有効期限日数
 */
function setValueWithExpireDays(
  key: string,
  value: string,
  days: number
): void {
  const expireDate: Date = new Date()
  expireDate.setDate(expireDate.getDate() + days)
  setValueByDate(key, value, expireDate)
}

/**
 * Cookie設定（有効期限1ヶ月）
 * @param key Cookieのkey
 * @param value Cookieのvalue
 */
function setValue1Month(key: string, value: string): void {
  const expireDate: Date = new Date()
  expireDate.setMonth(expireDate.getMonth() + 1)
  setValueByDate(key, value, expireDate)
}

/**
 * Cookie設定（有効期限分指定）
 * @param key Cookieのkey
 * @param value Cookieのvalue
 * @param minutes 有効期限分
 */
function setValueWithExpireMinutes(
  key: string,
  value: string,
  minutes: number
): void {
  const expireDate: Date = new Date()
  expireDate.setMinutes(expireDate.getMinutes() + minutes)
  setValueByDate(key, value, expireDate)
}

/**
 * Cookie設定
 * @param key Cookieのkey
 * @param value Cookieのvalue
 * @param expireDate Cookieの有効期限(Date型)
 */
function setValueByDate(key: string, value: string, expireDate: Date): void {
  setValue(key, value, expireDate.toUTCString())
}

/**
 * Cookie設定
 * @param key Cookieのkey
 * @param value Cookieのvalue
 * @param expireDate Cookieの有効期限
 */
function setValue(key: string, value: string, expireDate: string): void {
  document.cookie = `${key}=${value};expires=${expireDate};path=/;secure=true;SameSite=Strict; `
}

// #endregion

// #region Cookie削除

/**
 * 指定されたkeyのCookieを削除する。
 * @param key 取得したいCookieのkey
 */
function deleteCookie(key: string): void {
  document.cookie = `${key}=;max-age=0 `
}

// #endregion

// #endregion

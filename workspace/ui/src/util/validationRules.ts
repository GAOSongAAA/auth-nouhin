/**
 * 正規表現：半角英数字記号
 */
export const ALPHANUMERIC_NUMBER_SYMBOL: RegExp = /^[a-zA-Z0-9!-/:-@[-`{-~]*$/

/**
 * 正規表現：半角英数字記号以外、または、空白
 */
export const ALPHANUMERIC_NUMBER_OLD_SYMBOL: RegExp =
  /([^A-Za-z0-9!#$%&()*+,.:;=?@\[\]^{}\-/\_|'<>~]+$|\s)/

/**
 * 正規表現：半角英数字
 */
export const ALPHANUMERIC_NUMBER: RegExp = /^[A-Za-z0-9]+$/

/**
 * 正規表現：e-mail形式（x@y.z）
 */
export const ALPHANUMERIC_NUMBER_EMAIL: RegExp = /.+@.+\..+/

/**
 * 正規表現:数値のみ
 */
export const NUMBER_SYMBOL: RegExp = /^([0-9]\d*)$/

/**
 * 正規表現:0と正の整数のみ(先頭ゼロは認めない)
 */
export const NUMBER_SYMBOL_INCLUDE_ZERO: RegExp = /(^([1-9]\d*)$|^(0)$)/

/**
 * 正規表現:半角カナ始まり
 */
export const KANA_START: RegExp = /^[ｦ-ﾝ]/

/**
 * 正規表現:半角数字始まり
 */
export const NUMBER_START: RegExp = /^[0-9]/

/**
 * 正規表現:半角英字始まり
 */
export const ALPHABET_START: RegExp = /^[a-zA-Z]/

/**
 * 正規表現:上記3つ以外の記号始まり
 */
export const OTHER_START: RegExp = /^[^ｦ-ﾝ0-9a-zA-Z]/

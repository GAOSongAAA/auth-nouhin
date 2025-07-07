/**
 * 時間指定待機
 * @param ms 待機時間（ミリ秒）
 * @returns 待機Promise
 */
export function wait(ms: number): Promise<void> {
    return new Promise<void>((resolve) => {
        setTimeout(() => {
            resolve();
        }, ms)
    });
}

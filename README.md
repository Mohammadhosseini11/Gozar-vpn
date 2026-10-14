# Gozar VPN — Kotlin architecture starter

این پروژه اسکلت معماری برای توسعه Gozar با پشتیبانی واردکردن VLESS/VMess و Subscription است. موتور واقعی Xray/libv2ray هنوز داخل پروژه نیست؛ بنابراین این نسخه عمداً تونل ایجاد نمی‌کند و نباید به‌عنوان VPN عملیاتی منتشر شود.

## معماری

```text
com.gozar.vpn
├── MainActivity.kt                 # رابط Compose فعلی
├── model/
│   └── ServerProfile.kt            # مدل پروفایل
├── data/
│   ├── ProfileParser.kt             # پارس اولیه vless:// و vmess://
│   ├── SubscriptionImporter.kt      # دریافت لینک اشتراک HTTPS
│   └── ProfileStore.kt              # ذخیره‌سازی محلی ساده
└── vpn/
    ├── CoreEngine.kt                # مرز اتصال به موتور Xray/libv2ray
    └── GozarVpnService.kt           # چرخه عمر سرویس VPN اندروید
```

## مراحل بعدی

1. **انتخاب موتور:** بررسی Xray/libv2ray Android binding از پروژه v2rayNG. قبل از کپی یا استفاده، نسخه، ABIها، مجوزها و شرایط GPL-3.0 را بررسی کنید.
2. **تکمیل Parser:** لینک‌های share ممکن است transport، TLS/REALITY، SNI، fingerprint، path، host، serviceName و سایر پارامترها داشته باشند. parser فعلی فقط برای تشخیص اولیه است؛ تبدیل کامل و تست‌شده باید بر اساس فرمت موتور انجام شود.
3. **پیاده‌سازی CoreEngine:** ساخت/تبدیل config مورد نیاز موتور، شروع core، راه‌اندازی packet forwarding بین TUN و موتور، گزارش خطا و توقف قطعی.
4. **اتصال VpnService:** بعد از `VpnService.prepare()` و رضایت کاربر، سرویس را شروع کنید. سوکت خروجی موتور باید با `protect()` از مسیریابی داخل تونل خارج شود تا loop ایجاد نشود.
5. **وضعیت واقعی UI:** state اتصال را از رویدادهای موتور بگیرید؛ هرگز صرفاً با کلیک دکمه «متصل» نشان ندهید.
6. **Subscription امن:** فقط HTTPS، محدودیت حجم/زمان، مدیریت redirect، خطای شبکه، جلوگیری از log شدن لینک‌ها و امکان حذف/به‌روزرسانی اشتراک.
7. **آزمایش:** تست روی دستگاه واقعی، قطع شبکه، تغییر Wi‑Fi/دیتا، رد مجوز VPN، قطع سرویس، IPv4/IPv6 و DNS leak. تا وقتی packet forwarding واقعی آماده نشده، TUN را برقرار نکنید.

## اجرا

- پروژه را با Android Studio باز کنید.
- JDK 17 و Android SDK 35 نصب باشد.
- Gradle Sync و سپس Run.
- این نسخه، اسکلت توسعه است؛ UI موجود ممکن است هنوز به import/store/service متصل نشده باشد.

## منابع

- Android VPN guide: https://developer.android.com/develop/connectivity/vpn
- v2rayNG / Xray core Android: https://github.com/2dust/v2rayNG

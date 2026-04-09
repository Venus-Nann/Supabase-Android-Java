# 🚀 Supabase Android Java (Lite)

Android (Java) Application များတွင် Supabase Backend ကို လွယ်ကူလျင်မြန်စွာ ချိတ်ဆက်အသုံးပြုနိုင်ရန် ဖန်တီးထားသော Lightweight Library ဖြစ်ပါသည်။

---

## 📦 Installation

သင်၏ Project တွင် အသုံးပြုရန် အောက်ပါအဆင့်များအတိုင်း လုပ်ဆောင်ပါ။

### ၁။ JitPack Repository ထည့်သွင်းခြင်း
သင်၏ `settings.gradle` (သို့မဟုတ် Root `build.gradle`) ဖိုင်တွင် အောက်ပါအတိုင်း ထည့်သွင်းပါ။

```gradle
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```
 ### ၂။ Library Dependency ထည့်သွင်းခြင်း
သင်၏ App level build.gradle ဖိုင်တွင် အောက်ပါအတိုင်း ထည့်သွင်းပါ။
```gradle
dependencies {
    implementation 'com.github.Venus-Nann:Supabase-Android-Java:v1.0.8-beta'
}
```
## 🛠 How to Use (အသုံးပြုနည်း)
၁။ Client ကို စတင်ခြင်း (Initialize)
```java
SupabaseClient client = new SupabaseClient(context,"YOUR_SUPABASE_URL", "YOUR_ANON_KEY");
```
### Authentication 
Setup Authentication Service 
```java
SupabaseAuth auth = client.setAuth();
```
Signup (စာရင်းအသစ်သွင်းခြင်း)
```java
auth.signUp("example@email.com", "password123", new SupabaseListener<String>() {
    @Override
    public void onSuccess(String message) {
        // အကောင့်ဖွင့်ခြင်း အောင်မြင်လျှင်
        String userToken = message; //app ကိုသုံးနိုင်ရန် user token
    }

    @Override
    public void onError(String error) {
        // Error တက်လျှင် (မြန်မာလို ပြသပေးမည်)
    }
});
```
Login (ပြန်လည်၀င်ရောက်ခြင်း)
```java
auth.login("example@email.com", "password123", new SupabaseListener() {
    @Override
    public void onSuccess(String message) {
        // အကောင့်ဖွင့်ခြင်း အောင်မြင်လျှင်
        String userToken = message; //app ကိုသုံးနိုင်ရန် user token
    }

    @Override
    public void onError(String error) {
        // Error တက်လျှင် (မြန်မာလို ပြသပေးမည်)
    }
});
```
### Database
Setup database service 
```java
SupabaseDatabase db = client.setDatabase("YOUR_SUPABASE_TABLE_NAME");
```
Post data (data ပို့ခြင်း) 
```java
JSONObject data = new JSONObject();
data.put("name", "Yadanar Screen Protector");
data.put("category", "Glass");


db
.insert(data)
.execute(new SupabaseListener<String>() {
    @Override
    public void onSuccess(String message) {
        // Data တင်ခြင်းအောင်မြင်လျှင်
    }

    @Override
    public void onError(String error) {
        // Error တက်လျှင် 
    }
});
```
Get data (data ယူခြင်း)
```java
db
.select("YOUR_DATABASE_COLUMN")
.[YOUR_FILTER_METHODS]
.execute(new SupabaseListener<String>() {
    @Override
    public void onSuccess(String message) {
        // Data ယူခြင်းအောင်မြင်လျှင်
    }

    @Override
    public void onError(String error) {
        // Error တက်လျှင် 
    }
});
```
[FILTER_METHODS]

| Function        | Description                                                                 | Example                              |
|-----------------|-----------------------------------------------------------------------------|--------------------------------------|
| eq(col, val)    | Equals - တန်ဖိုး တထပ်တည်းတူတာကို ရှာရန်                                   | .eq("id", "10")                      |
| neq(col, val)   | Not Equal - တန်ဖိုး မတူညီတာကို ရှာရန်                                     | .neq("status", "off")                |
| gt(col, val)    | Greater Than - ထက် ကြီးတာကို ရှာရန်                                       | .gt("price", "5000")                 |
| gte(col, val)   | Greater Than or Equal - ကြီးတာ (သို့) ညီတာကို ရှာရန်                      | .gte("age", "18")                    |
| lt(col, val)    | Less Than - ထက် ငယ်တာကို ရှာရန်                                           | .lt("stock", "5")                    |
| lte(col, val)   | Less Than or Equal - ငယ်တာ (သို့) ညီတာကို ရှာရန်                          | .lte("discount", "20")               |
| ilike(col, val) | Case-insensitive search - အကြီးအသေး မခွဲဘဲ စာသားရှာရန်                   | .ilike("name", "iphone")             |
| is(col, val)    | NULL / TRUE / FALSE စတာတွေ စစ်ရန်                                          | .is("deleted_at", "null")            |
| in(col, list)   | In List - စာရင်းထဲမှာ ပါဝင်တာကို ရှာရန်                                     | .in("cat", "Glass,Case")             |
| order(col, asc) | Order By - အစီအစဉ်စီရန် (True = Asc, False = Desc)                        | .order("price", false)               |
| limit(count)    | Limit - ရလဒ်အရေအတွက် ကန့်သတ်ရန်                                           | .limit(5)                            |
| offset(count)   | Offset - Data အချို့ကို ကျော်ရန် (Pagination)                               | .offset(10)                          |
| or(filters)     | Logical OR - "ဒါမှမဟုတ်" စနစ်ဖြင့် စစ်ထုတ်ရန်                              | .or("id.eq.5,name.eq.Aung")          |

Update data (data ပြင်ခြင်း)
```java
JSONObject updatedData = new JSONObject();
try {
    updatedData.put("price", 18000); // ဈေးနှုန်းအသစ်
    updatedData.put("status", "in_stock");
} catch (JSONException e) {
    e.printStackTrace();
}

db
.update(updatedData)
.eq("id", "10") // ဘယ် ID ကို ပြင်မှာလဲဆိုတာ Filter ပေးရပါမယ်
.execute(new SupabaseListener<String>() {
          @Override
          public void onSuccess(String message) {
              //Data ပြင်ဆင်ခြင်းအောင်မြင်လျှင်
          }

          @Override
          public void onError(String error) {
              //မအောင်မြင်လျှင်
          }
 });
```
Delete data (data ဖျတ်ခြင်း)
```java
db
.delete()
.eq("id", "10") // ဘယ်ဟာကိုဖျတ်မှာလဲ Filter ပေးရပါမယ်
.execute(new SupabaseListener<String>() {
          @Override
          public void onSuccess(String message) {
              //Data ဖျတ်ခြင်းအောင်မြင်လျှင်
          }

          @Override
          public void onError(String error) {
              //မအောင်မြင်လျှင်
          }
 });
 ```
### Storage
Setup storage service 
```java
SupabaseBucket storage = client.setBucket("YOUR_SUPABASE_BUCKET_NAME");
```
Upload File (file တင်ခြင်း)
```java
storage.uploadFile("YOUR_FILE_PATH","YOUR_FILE_NAME",new SupabaseListener<String>() {
          @Override
          public void onSuccess(String message) {
              //Upload တင်ခြင်းအောင်မြင်လျှင်
              String fileUrl = storage.geyPublicUrl("YOUR_FILE_NAME");//file link ရယူရန်
          }

          @Override
          public void onError(String error) {
              //မအောင်မြင်လျှင်
          }
 });
```
File Delete (file ဖျတ်ခြင်း)
```java
storage.deleteFile("YOUR_FILE_NAME",new SupabaseListener<String>() {
          @Override
          public void onSuccess(String message) {
              //File ဖျတ်ခြင်းအောင်မြင်လျှင်
              
          }

          @Override
          public void onError(String error) {
              //မအောင်မြင်လျှင်
          }
 });
```
## ⚠️ Requirements
- Android API Level 21+ (Lollipop)
- Internet Permission (AndroidManifest.xml တွင် ထည့်ရန် မမေ့ပါနှင့်)

## 👨‍💻 Developer
- **Created by**: Venus-Nann  
- **YouTube**: ITM Channal  
- **Project**: Supabase Android Java

## 📄 License
This library is licensed under the **MIT License**.

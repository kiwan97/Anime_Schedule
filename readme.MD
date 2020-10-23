## Anime Scheduler
안드로이드 애니편성표 앱 입니다.<br>

MVC로 구성되어 있습니다.<br>

애니시아에서 제공하는 REST API를 사용했습니다. [링크](https://www.anissia.net/)<br>

# 사용된 기술
	- [REST API](https://ko.wikipedia.org/wiki/REST)
		[애니시아](https://www.anissia.net/?m=1&b=4)에서 제공하는 REST API사용

# 세부설명

# REST API 

애니시아에서 제공하는 API는 3가지 입니다.<br>
현재 방영중인 애니메이션 목록, 특정 애니메이션 정보, 과거 애니메이션의 정보<br>
그 중 저는 현재 방영중인 애니메이션, 특정 애니메이션 정보를 사용했습니다.<br>

먼저 현재 방영중인 애니메이션 부분을 GET하는 코드를 설명드리겠습니다.<br>

Task.java<br>
메인 엑티비티에서 API호출값을 가져올 때 [AsyncTask](https://itmining.tistory.com/7)를 통해 가져옵니다.<br>
비교적 작은 작업을 백그라운드에서 처리할 때 유리한 AsyncTask를 사용해 result를 가져옵니다.<br>

AsyncTask는 다음과 같습니다.<br>

```
public class Task extends AsyncTask<String, Void, String> {
    private String str,receiveMsg;
    String WeekDay[] = {"\"SUN\":","\"MON\":","\"TUE\":","\"WED\":","\"THR\":","\"FRI\":","\"SAT\":"};
    @Override
    protected String doInBackground(String... params) {
        URL url = null;
        receiveMsg = "{";
        for(int i=0;i<7;i++) {
            try {
                receiveMsg+=  WeekDay[i];
                url = new URL("https://www.anissia.net/anitime/list?w="+Integer.toString(i));

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                if (conn.getResponseCode() == conn.HTTP_OK) {
                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuffer buffer = new StringBuffer();
                    while ((str = reader.readLine()) != null) {
                        buffer.append(str);
                    }
                    receiveMsg += buffer.toString();
                    if(i!=6) receiveMsg+=',';
                    else receiveMsg+='}';
                    Log.i("receiveMsg : ", receiveMsg);

                    reader.close();
                } else {
                    Log.i("통신 결과", conn.getResponseCode() + "에러");
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return receiveMsg;
    }
}
```
doInBackground()메소드에서 새로 만든 스레드의 작업이 명시되어 있습니다.<br>
먼저 애니시아의 API중 현재 방영중인 애니메이션 리스트들을 가져옵니다.<br>
API주소는 다음과 같습니다.<br>
https://www.anissia.net/anitime/list?w="+Integer.toString(i)<br>
w=[숫자]에서 숫자 부분은 해당 요일에 해당하는 애니메이션 리스트를 가져옵니다.(ex w=0은 월요일)<br>

HttpURLConnection을 통해 해당 url API의 GET(default)를 호출합니다.<br>
InputStreamReader를 통해 해당 conn의 input 데이터를 가져옵니다.<br>
이번에 InputStreamReader을 통해 BufferedReader를 만듭니다.<br>
buffer에 BufferedReader에 있는 내용을 옮깁니다.<br>
그리고 receiveMsg에 붙여줍니다.<br>
월요일부터 일요일까지 모든 애니메이션 리스트를 receiveMsg에 붙였다면 해당 값을 리턴해줍니다.<br>

다음은 각각의 애니메이션의 세부정보를 알려주는 API호출 부분을 보겠습니다.<br>

```
public class subTask extends AsyncTask<String, Void, String> {
    private String str,receiveMsg;
    @Override
    protected String doInBackground(String... params) {
        URL url = null;
        String i = params[0];
        try {
            url = new URL("https://www.anissia.net/anitime/cap?i="+i);
            System.out.println("!!"+i + "!!");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            if (conn.getResponseCode() == conn.HTTP_OK) {
                InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                BufferedReader reader = new BufferedReader(tmp);
                StringBuffer buffer = new StringBuffer();
                while ((str = reader.readLine()) != null) {
                    buffer.append(str);
                }
                receiveMsg = buffer.toString();
                Log.i("receiveMsg : ", receiveMsg);

                reader.close();
            } else {
                Log.i("통신 결과", conn.getResponseCode() + "에러");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return receiveMsg;
    }
}
```
doInBackground의 인자로 전달된 params[0]에<br>
해당 애니메이션 고유의 코드가 적혀있습니다.<br>
"https://www.anissia.net/anitime/cap?i="+i<br>
해당 url로 GET을 호출합니다.<br>
그렇게 나온 결과를 receiveMsg에 옮기고 이를 리턴해줍니다.<br>

이제 실제로 리턴된 값들이 사용되는 부분을 살펴보겠습니다.<br>

메인 엑티비티의 onCreate(...)부분입니다.<br>

```
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        overridePendingTransition(R.anim.anim_slide_in_right,R.anim.anim_slide_out_left);
        context=this;
        for(int i=0;i<7;i++){
            Msg.add(new ArrayList<String>());
            Msg2.add(new ArrayList<String>());
        }
        try{
            Json = new Task().execute().get();
            getAnimeTitle(Json);
            getAnimeTime(Json);
        }
        catch (InterruptedException e) {
            e.printStackTrace(); }
        catch (ExecutionException e) {
            e.printStackTrace();

        }
        mViewpager = findViewById(R.id.viewPager);
        pagerAdapter = new TextViewPagerAdapter(this,Msg,Msg2,Json);
        mViewpager.setAdapter(pagerAdapter);
        mViewpager.setCurrentItem(35+(Calendar.getInstance().get(Calendar.DAY_OF_WEEK))-1,false);
    }
```

Task의 get()을 통해 반환된 모든 요일의 애니메이션 리스트를 Json에 담아주었습니다.<br>
그리고 title과 방영시간을 각각 Msg,Msg2에 저장해주어 <br>
메인 뷰인 viewPager에 나타내주기 위해<br>
viewPager의 Adapter에 넘겨줍니다.<br>

다음은 viewPager의 Adapter인 TextViewPagerAdapter의 구현 부분입니다.<br>

```
public TextViewPagerAdapter(Context context,ArrayList<ArrayList<String> > titles,ArrayList<ArrayList<String> > times, String Json) {
            mContext = context ;
            anime_titles = titles;
            anime_times = times;
            this.Json = Json;
        }
```
먼저 생성자에서 전달된 title, 방영시간, 모든 Json이 다음과 같습니다.<br>

viewPager의 각각의 page를 생성하게 될 instantiateItem(...)메소드는 다음과 같습니다.<br>

```
@Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = null ;
            ListView listView;
            final int pos = position%7;
            if (mContext != null) {
                // LayoutInflater를 통해 "/res/layout/page.xml"을 뷰로 생성.
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.view_page, container, false);

                listView = view.findViewById(R.id.listView);
                listView.setAdapter(new MyAdapter(mContext,anime_titles.get(pos),anime_times.get(pos)));
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent = new Intent(mContext,DetailInfo.class);
                        intent.putExtra("json",Json);
                        intent.putExtra("title",i);
                        intent.putExtra("weekday",pos);
                        mContext.startActivity(intent);
                    }
                });
                TextView weekday = view.findViewById(R.id.weekday);
                weekday.setText(WeekDay2[pos]);

                //옆에 시간부분 만들기

            }

            // 뷰페이저에 추가.
            container.addView(view) ;

            return view ;
        }
```
page는 ListView가 차지합니다.<br>
각각의 요일별(position) 적절한 데이터를 MyAdapter에 넘겨주고<br>
해당 Adapter를 listView에 지정합니다.<br>
그리고 각각의 listView 아이템은 하나의 애니메이션에 해당합니다.<br>
만약 애니메이션을 클릭할 경우 DetailInfo 액티비티가 Intent를 통해 실행되어<br>
해당 액티비티의 뷰가 열립니다.<br>

listView의 Adapter구현 부분을 잠시 살펴보겠습니다.<br>
```
public class MyAdapter extends BaseAdapter {
    Context mContext = null;
    LayoutInflater mLayoutInflater = null;
    ArrayList<String> sample;
    ArrayList<String> sample2;
    public MyAdapter(Context context, ArrayList<String> data,ArrayList<String> data2) {
        mContext = context;
        sample = data;
        sample2 = data2;

        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return sample.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public String getItem(int position) {
        return sample.get(position);
    }

    @Override
    public View getView(int position, View converView, ViewGroup parent){
        View view = mLayoutInflater.inflate(R.layout.listview_component,null);

        TextView textview = view.findViewById(R.id.title_item);
        textview.setText(sample.get(position));

        textview = view.findViewById(R.id.time_item);
        textview.setText(sample2.get(position).substring(0,2) +":"+sample2.get(position).substring(2,4));
        //여기 부터textview.setText()
        return view;
    }
}
```
sample은 애니메이션 제목들, sample2는 애니메이션 방영시간을 나타냅니다.<br>
전체 component갯수는 getCount()의 리턴값이므로 리턴값을 모든 애니메이션 갯수를 리턴해줍니다.<br>
getView에서 각각의 component내의 textview에 애니메이션 제목, 방영시간을 넣어줍니다.<br>

다시 TextViewPagerAdapter로 돌아와서<br>
listview의 onItemClick 즉 각각의 component들이 클릭되었을때<br>
실행되는 DetailInfo 액티비티에 대해서 살펴보겠습니다.<br>


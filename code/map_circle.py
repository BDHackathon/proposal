#!/usr/bin/env python
# coding: utf-8

# In[2]:


pip install folium geopandas


# In[55]:


import folium
import geopandas as gpd
import pandas as pd
from IPython.display import display

###############필수 설정 항목######################
year = input("2018~2021 중 입력하시오.\nyear : ")  #원하는 연도 설정
count_or_weight = input("count 또는 weight를 입력하시오.\nans : ")#개수 또는 무게 설정
################################################
print(year+"_"+count_or_weight)

main_data = 'year_'+year+'_'+count_or_weight

geojson_file = 'TL_SCCO_SIG.json'
gdf = gpd.read_file(geojson_file)

center_lat, center_lon = 36.5, 127.5

map_korea = folium.Map(location=[center_lat, center_lon], zoom_start=7)

folium.GeoJson(gdf).add_to(map_korea)

Data = 'trash_location.csv'
df = pd.read_csv(Data)
SCM = ['지역',main_data,'str_la','str_lo','end_la','end_lo']
df = df[df['str_la'].notnull()] #위도,경도 없는 지역 거르기
df = df.loc[:,SCM]

for col, row in df.iterrows():
    circle_lat, circle_lon = (row['str_la']+row['end_la'])/2, (row['str_lo']+row['end_lo'])/2
    circle_name = row['지역']
    circle_popup = f'<strong>{circle_name}</strong>'
    if count_or_weight == 'count':
        circle_radius = row[main_data]*2
    else:
        circle_radius = row[main_data]*20
    folium.Circle(location=[circle_lat, circle_lon], radius=circle_radius, popup=circle_popup,
              color='red', fill=True, fill_color='red').add_to(map_korea)
    #folium.CircleMarker(location=[circle_lat, circle_lon], radius=1, color='black', fill=True, fill_color='black').add_to(map_korea)
    #원 중앙에 점을 찍는 코드. 시각적으로 뭐가 나은지 모르겠어서 남겨놓음
display(map_korea)


# In[ ]:





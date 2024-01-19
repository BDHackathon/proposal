#!/usr/bin/env python
# coding: utf-8

# In[1]:


pip install folium geopandas


# In[7]:


import folium
import geopandas as gpd
from IPython.display import display

geojson_file = 'Desktop/TL_SCCO_SIG.json'#대한민국 데이터 파일을 json형태로 가지고있어야함
gdf = gpd.read_file(geojson_file)

center_lat, center_lon = 36.5, 127.5

map_korea = folium.Map(location=[center_lat, center_lon], zoom_start=7)

folium.GeoJson(gdf).add_to(map_korea)

circle_lat, circle_lon = 37.5, 127.0
circle_name = '서울'
circle_popup = f'<strong>{circle_name}</strong>'
circle_radius = 5000

folium.Circle(location=[circle_lat, circle_lon], radius=circle_radius, popup=circle_popup,
              color='red', fill=True, fill_color='red').add_to(map_korea)

display(map_korea)


# In[ ]:





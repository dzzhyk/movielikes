import requests
import csv
from tqdm import tqdm
import json
from fake_useragent import UserAgent

'''
从tmdb抓取电影信息
'''

u = UserAgent(path="./fake_useragents.json")
csv_title = ['movieId', 'title_english', 'title_chinese',
             'genres', 'release', 'runtime', 'overview', 'poster_path']

if __name__ == "__main__":
    with open("/Users/dzzhyk/Downloads/ml-latest/movies.csv", "r", encoding="utf-8") as movies, open("/Users/dzzhyk/Downloads/ml-latest/links.csv", "r", encoding="utf-8") as links, open("./grab_movies.log", "a", encoding="utf-8") as flog, open("./grab_movies.csv", "w", newline="", encoding="utf-8") as fcsv:
        writer = csv.writer(fcsv)
        writer.writerow(csv_title)
        links.readline()
        movies.readline()
        ua = u.chrome
        for i in tqdm(range(58098)):
            try:
                tmp_movie = movies.readline().split(",")
                tmp_link = links.readline().split(",")
                movieId, tmdbId = tmp_link[0], tmp_link[2]

                r = requests.get(f"http://api.themoviedb.org/3/movie/{tmdbId}?api_key=f332153ed74f1e3cd4b6f153b938badb&language=zh-CN", timeout=5, headers={'User-Agent': ua})
                data = json.loads(r.text)

                title_english = tmp_movie[1]
                title_chinese = data['title'].strip()
                genres = tmp_movie[2].replace("\n", "")
                release = data['release_date'].strip()
                runtime = data['runtime']
                overview = data['overview'].strip().replace("　", "").replace(" ", "").replace(",", "，").replace("\n", "")
                poster_path = data['poster_path']

                writer.writerow([movieId, title_english, title_chinese, genres, release, runtime, overview, poster_path])
                flog.write(f"[DONE] {movieId}\n")

            except Exception as e:
                flog.write(f'[ERROR] {movieId} "{e}"\n')
                print(e)

            flog.flush()

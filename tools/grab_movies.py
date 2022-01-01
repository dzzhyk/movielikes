import requests
from tqdm import tqdm
import json
from fake_useragent import UserAgent

'''
从tmdb抓取电影信息
'''
proxys = {
    "http": "http://127.0.0.1:7890",
    "https": "http://127.0.0.1:7890"
}
u = UserAgent(path="./fake_useragents.json")

if __name__ == "__main__":
    with open("./movies_links.txt", "r", encoding="utf-8") as fin, open("./grab_movies.log", "a", encoding="utf-8") as flog, open("./grab_movies.txt", "a", encoding="utf-8") as fout:
        ua = u.chrome
        for i in tqdm(range(58098)):
            try:
                input_line = fin.readline()
                # 193886|Leal (2018)|Action&&Crime&&Drama|7606620|540871
                tmp_list = input_line.split("|")
                movieId, title, genres, imdbId, tmdbId = tmp_list[0], tmp_list[1], tmp_list[2], tmp_list[3], tmp_list[4].replace("\n", "")
                r = requests.get(f"http://api.themoviedb.org/3/movie/{tmdbId}?api_key=f332153ed74f1e3cd4b6f153b938badb", timeout=10, headers={'User-Agent': ua}, proxies=proxys)
                data = json.loads(r.text)

                release = data['release_date'].strip()
                runtime = str(data['runtime'])

                overview = data['overview'].strip().replace("　", "").replace("\n", "").replace("\r", "")
                poster_path = "null"
                if data['poster_path'] is not None:
                    poster_path = data['poster_path']

                fout.write("|".join([movieId, title, genres, imdbId, tmdbId, release, runtime, overview, poster_path]) + "\n")
                flog.write(f"[DONE] {movieId}\n")
            except Exception as e:
                flog.write(f'[ERROR] {movieId} "{e}"\n')
            fout.flush()
            flog.flush()